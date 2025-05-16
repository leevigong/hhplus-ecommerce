package kr.hhplus.be.server.infra.sales;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.sales.ProductSalesInfo;
import kr.hhplus.be.server.support.cache.CacheNames;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RedisProductSalesRepository {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String MEMBER_PREFIX = "product:";
    private static final String TMP_PREFIX = "tmp:";

    private final RedisTemplate<String, String> redisTemplate;

    public RedisProductSalesRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String buildCacheKey(LocalDate date) {
        return CacheNames.POPULAR_PRODUCT_SALES + ":" + date.format(DATE_FORMAT);
    }

    public void add(List<OrderItem> items) {
        String redisKey = buildCacheKey(LocalDate.now());

        items.forEach(item ->
                redisTemplate.opsForZSet()
                        .incrementScore(redisKey, MEMBER_PREFIX + item.getProductId(), item.getQuantity())
        );

        if (!redisTemplate.hasKey(redisKey)) {
            redisTemplate.expire(redisKey, Duration.ofDays(31));
        }
    }

    public List<ProductSalesInfo.Popular> getTopSales(LocalDate date, int limit) {
        String redisKey = buildCacheKey(date);

        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(redisKey, 0, limit - 1);

        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyList();
        }

        return tuples.stream()
                .filter(t -> t.getScore() != null)
                .map(t -> {
                    String member = t.getValue();
                    if (member.startsWith(MEMBER_PREFIX)) {
                        member = member.substring(MEMBER_PREFIX.length());
                    }
                    Long productId = Long.valueOf(member);
                    Long score = t.getScore().longValue();
                    return ProductSalesInfo.Popular.of(productId, score);
                })
                .toList();
    }

    public List<ProductSalesInfo.Popular> getTopSalesRange(LocalDate startDate, LocalDate endDate, int top) {
        if (startDate.isAfter(endDate)) {
            LocalDate tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }

        // 기간-합산 랭킹 ZSET 키
        String rangeKey = String.format("%s:%s-%s:top%d",
                CacheNames.POPULAR_PRODUCT_SALES,
                startDate.format(DATE_FORMAT),
                endDate.format(DATE_FORMAT),
                top);

        // 캐시에 이미 있으면 바로 사용
        if (redisTemplate.hasKey(rangeKey)) {
            return extractTop(rangeKey, top);
        }

        // 없으면 ZUNIONSTORE 로 합산 (존재하는 키만)
        List<String> dayKeys = startDate.datesUntil(endDate.plusDays(1))
                .map(this::buildCacheKey)
                .filter(redisTemplate::hasKey)
                .toList();

        if (dayKeys.isEmpty()) {
            return Collections.emptyList();
        }

        String tmpKey = TMP_PREFIX + UUID.randomUUID();
        if (dayKeys.size() == 1) {
            redisTemplate.opsForZSet()
                    .unionAndStore(dayKeys.get(0), Collections.emptyList(), tmpKey);
        } else {
            redisTemplate.opsForZSet()
                    .unionAndStore(dayKeys.get(0), dayKeys.subList(1, dayKeys.size()), tmpKey);
        }

        // 합산 결과를 rangeKey 로 RENAME & TTL 5m
        redisTemplate.rename(tmpKey, rangeKey);
        redisTemplate.expire(rangeKey, Duration.ofMinutes(5));

        return extractTop(rangeKey, top);
    }

    // ZSET → DTO 리스트 변환 공통 함수
    private List<ProductSalesInfo.Popular> extractTop(String zsetKey, int top) {
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(zsetKey, 0, top - 1);

        if (tuples == null || tuples.isEmpty()) return Collections.emptyList();

        return tuples.stream()
                .map(t -> {
                    String member = t.getValue().substring(MEMBER_PREFIX.length());
                    return ProductSalesInfo.Popular.of(Long.valueOf(member), t.getScore().longValue());
                })
                .collect(Collectors.toList());
    }

    public List<ProductSalesInfo.Popular> getAllSales(LocalDate date) {
        String redisKey = buildCacheKey(date);

        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(redisKey, 0, -1);

        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyList();
        }

        return tuples.stream()
                .filter(t -> t.getScore() != null)
                .map(t -> {
                    String member = t.getValue();
                    if (member.startsWith(MEMBER_PREFIX)) {
                        member = member.substring(MEMBER_PREFIX.length());
                    }
                    Long productId = Long.valueOf(member);
                    Long score = t.getScore().longValue();
                    return ProductSalesInfo.Popular.of(productId, score);
                })
                .collect(Collectors.toList());
    }

}
