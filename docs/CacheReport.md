# Redis 캐시 보고서


## 문제 정의
반복 조회가 많은 엔드포인트의 응답을 메모리에 저장하면 데이터베이스 부하를 효과적으로 분산시킬 수 있다.  
따라서 무차별적인 캐싱은 데이터 불일치를 초래할 수 있으므로 **읽기 빈도·지연·변경 주기**를 기준으로 캐시 적용 대상을 선별한다.

**인기상품 조회 API**
- 요청 빈도: 최다 트래픽 엔드포인트
- 데이터 갱신 주기: 매 자정마다
- 쿼리 비용: `popular_product` 테이블 단건 조회 – 평균 687ms
따라서, 요청 빈도가 높고, 하루 동안 데이터가 변경되지 않는 특성상 캐시 적용에 매우 적합한 기능으로 판단

## 기술 비교 평가
| 구분 | 설명 | 장점                   | 단점 · 트레이드오프 |
|------|------|----------------------|---------------------|
| **A. DB Only** | 기존 구조 유지 | 현재 변경사항 x, 안정성 확보    | SLA 미달, DB 부하 증가 |
| **B. Read‑Through Redis**<br>*(선택)* | 요청 시 캐시 없으면 DB 조회 후 저장| 구현 간단, 무효화 쉬움, 비용 낮음 | 초기 Miss 시 “thundering herd” 가능 |
| **C. Write‑Behind + MQ** | 배치·이벤트로 Redis 갱신 | DB 부하 최소, Hot‑key 완화 | MQ 운영 복잡도↑, 최종일관성 이슈 |
> **선택 사유**: 현재 상황대비 **B안**이 효과가 가장 크다. 트래픽이 두 배 이상 증가하면 C안을 재검토한다.

## 인기상품 조회 캐싱 전략
### 아키텍처

```
Client ──► Spring API ──► DB
            ▲    │
            │    └───► Redis (@Cacheable)
            └─────────► Batch job (00:00) ──► Redis (@CachePut)
```
- **Read‑Through 캐시**: `@Cacheable` → Redis Miss 시 DB 조회 후 저장한다.
- **Write‑Through 배치 갱신**: 매일 00:00, 집계 후 `@CachePut` 로 Redis 갱신한다.

### TTL(Time To Live)
인기상품 캐시 TTL은 25시간으로 설정했다.

매 자정에 갱신하지만 24 시간 + 1 시간의 보호 구간으로 잡은 이유는 다음과 같다.
- 인기 상품 집계 배치는 매 자정(00:00)마다 돌지만, 배치가 지연되거나 재시작되는 경우가 있다.
- TTL을 정확히 24 시간으로 두면, 배치가 조금이라도 늦어질 때 캐시가 먼저 만료되어 “캐시 공백” 이 생기고, 동시에 다수 요청이 DB로 몰려 스탬피드가 발생할 수 있다.
- 24 시간에 여유 1 시간을 더해 25 시간으로 설정하면, 배치가 최대 1 시간까지 지연되어도 기존 캐시가 살아 있어 공백을 피할 수 있다.

```java
@Getter
public enum CacheType implements Cacheable {

    POPULAR_PRODUCTS(CacheNames.POPULAR_PRODUCTS, Duration.ofHours(25)), // TTL 25시간
    PRODUCT(CacheNames.PRODUCT, Duration.ofDays(7))
    ;

    private final String cacheName;
    private final Duration ttl;

    CacheType(String cacheName, Duration ttl) {
        this.cacheName = cacheName;
        this.ttl = ttl;
    }
}
```
### 캐시 적용
```java
@Transactional(readOnly = true)
@Cacheable(cacheNames = CacheNames.POPULAR_PRODUCTS, key = "#rankingScope") // 캐시 적용
public List<ProductSalesRankInfo> getProductSalesRank(String rankingScope) {
    List<ProductSalesRank> productSalesRanks = productSalesRankRepository.findByRankingScope(RankingScope.from(rankingScope));

    return productSalesRanks.stream()
            .map(rank -> ProductSalesRankInfo.from(rank))
            .collect(Collectors.toList());
}
```

### 테스트
```java
@Test
void 캐시적용하면_레포지토리_호출은_1번만_된다() {
    // given
    String scope = RankingScope.THREE_DAYS.name();

    // when
    productService.getProductSalesRank(scope); // 캐시 x, DB 0
    productService.getProductSalesRank(scope); // 캐시 0, DB x
    productService.getProductSalesRank(scope); // 캐시 0, DB x
    productService.getProductSalesRank(scope); // 캐시 0, DB x

    // then
    verify(productSalesRankRepository, times(1))
            .findByRankingScope(RankingScope.THREE_DAYS);
}
```
캐시 적용 전 테스트 실패: DB 4번 호출에 따라 DB에 4번 접근
![img.png](img.png)
캐시 적용 후 테스트 성공: DB 4번 호출하지만 DB에는 1번 접근
![img_1.png](img_1.png)

### 캐싱 성능 비교
캐시 성능을 검증하기 위해 K6을 사용하였다. [k6 부하 스크립트](https://github.com/leevigong/hhplus-ecommerce/blob/a55e872dc56605083d71085c560becf3e20df411/k6/popularProducts.js)

캐싱 적용 전
```java
@Transactional(readOnly = true)
public List<ProductSalesRankInfo> getProductSalesRank(String rankingScope) {
    List<ProductSalesRank> productSalesRanks = productSalesRankRepository.findByRankingScope(RankingScope.from(rankingScope));

    return productSalesRanks.stream()
            .map(rank -> ProductSalesRankInfo.from(rank))
            .collect(Collectors.toList());
}
```

캐싱 적용 후
```java
@Transactional(readOnly = true)
@Cacheable(cacheNames = CacheNames.POPULAR_PRODUCTS, key = "#rankingScope")
public List<ProductSalesRankInfo> getProductSalesRank(String rankingScope) {
    List<ProductSalesRank> productSalesRanks = productSalesRankRepository.findByRankingScope(RankingScope.from(rankingScope));

    return productSalesRanks.stream()
            .map(rank -> ProductSalesRankInfo.from(rank))
            .collect(Collectors.toList());
}
```

**부하 프로필 (단계별 VU)**
- 10 초 동안 10 VU (웜 업)
- 10 초 동안 50 VU
- 10 초 동안 100 VU
- 10 초 동안 100 VU (유지)
- 10 초 동안 50 VU
- 10 초 동안 10 VU (쿨다운)


#### 테스트 결과

| 항목                       | 캐싱 적용 전 | 캐싱 적용 후 | 차이 & 의미                                   |
|----------------------------|-----------------------|------------------------|-----------------------------------------------|
| 총 요청 수 (`http_reqs`)   | 1,918 req             | 3,232 req              | ▲ 68.51 % 증가 – 캐싱으로 요청 처리량 개선     |
| 실패율 (`http_req_failed`) | 0 % (0건)             | 0 % (0건)              | 변동 없음 – 안정적                            |
| 평균 응답 (`avg`)          | 687.5 ms              | 4.53 ms                | ▼ 99.34 % 감소 – 성능 크게 개선               |
| 최소 응답 (`min`)          | 3.19 ms               | 0.653 ms               | ▼ 79.53 % 감소 – 최소 지연 단축               |
| 중간값 응답 (`med`)        | 330.24 ms             | 3.33 ms                | ▼ 98.99 % 감소 – 전반적 응답 개선             |
| p90 응답                  | 1.8 s                 | 8.71 ms                | ▼ 99.52 % 감소 – 상위 지연 대폭 개선          |
| p95 응답                  | 2.71 s                | 11.3 ms                | ▼ 99.58 % 감소 – 대부분 요청 지연 개선        |
| 최대 응답 (`max`)          | 5.49 s                | 169.27 ms              | ▼ 96.92 % 감소 – 병목 제거                    |
| 초당 요청 수 (`req/s`)     | 27.19 req/s           | 45.71 req/s            | ▲ 68.10 % 증가 – 처리 능력 향상               |
| 데이터 수신량              | 869 kB                | 1.5 MB                 | ▲ 72.61 % 증가 – 처리량 증가                  |
| 데이터 송신량              | 246 kB                | 414 kB                 | ▲ 68.29 % 증가 – 처리량 증가                  |
| VU 피크                    | 100                   | 100                    | 동일 – 부하 조건 동일                         |
| Iteration drop             | 없음                  | 없음                   | 변동 없음                                     |


### 결론
기존 트래픽 2배까지 대응 가능한 여유를 확보 가능하다.
