# 시퀀스 다이어그램(Sequence Diagram)

---

- [1. 잔액](#1-잔액)
- [2. 상품](#2-상품)
- [3. 주문/결제](#3-주문결제)
- [4. 선착순 쿠폰](#4-선착순-쿠폰)
- [5. 인기 판매 상품](#5-인기-판매-상품)

## 1. 잔액
### 잔액 충전
```mermaid
sequenceDiagram
    actor User as 사용자
    participant UserPoint as 사용자 포인트
    User ->>+ UserPoint: 잔액 충전 요청(userId, amount)
    Note over UserPoint: 사용자/충전 금액 유효성 검증

    alt 검증 실패
        UserPoint -->> User: 충전 실패
    else 검증 성공
        UserPoint ->> UserPoint: 잔액 충전
        UserPoint -->>- User: 충전 성공 (잔액 + 충전금액)
    end
```
### 잔액 조회
```mermaid
sequenceDiagram
    actor User as 사용자
    participant UserPoint as 사용자 포인트

    User ->>+ UserPoint: 잔액 조회 요청(userId)
		Note over UserPoint: 사용자 유효성 검증
		
    alt 검증 실패 
        UserPoint -->> User: 조회 실패
    else 검증 성공
        UserPoint ->> UserPoint: 잔액 데이터 조회
        UserPoint -->>- User: 조회 성공 (보유 잔액)
    end
```

## 2. 상품
### 상품 조회
```mermaid
sequenceDiagram
  actor User as 사용자
  participant Product as 상품

  User ->>+ Product: 상품 조회 요청(productId)
  Note over Product: 상품 유효성 검증
  
  alt 검증 실패
    Product -->> User: 상품 조회 실패
  else 검증 성공
    Product ->> Product: 상품 데이터 조회
    Product -->>- User: 상품 조회 성공 (이름, 가격, 재고수량, 카테고리)
  end
```

## 3. 주문/결제
### 주문/결제 개요
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Order as 주문
    participant Payment as 결제
    participant DataPlatform as 데이터플랫폼

    User ->>+ Order: 주문 요청(userId, productIds)
    Note over Order: 상품/재고 검증

    alt 검증 실패
        Order -->> User: 주문 실패
    else 검증 성공
        Order ->>+ Payment: 결제 요청(orderId, couponId)
        Note over Payment: 쿠폰/잔액 유효성 검증 및 처리
        alt 검증 실패
            Payment -->> Order: 결제 실패
            Order -->> User: 주문 실패
        else 검증 성공
            Payment -->>- Order: 결제 성공
            Order -->> User: 주문 완료
            Order --)- DataPlatform: 주문/결제 데이터 저장
        end
    end
```

### 상품 주문 상세
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Order as 주문
    participant Product as 상품

    User ->>+ Order: 주문 요청(userId, productIds)

    Order ->>+ Product: 상품 정보/재고 확인
    Product -->>- Order: 상품/재고 확인 완료

    alt 재고 없음
        Order -->> User: 주문 실패
    else 재고 있음
        Order ->>+ Product: 재고 할당
        Product -->>- Order: 재고 할당 완료
        Order ->> Order: 주문 정보 저장
        Order -->>- User: 주문 완료 (orderId 반환)
    end
```

### 상품 결제 상세
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Payment as 결제
    participant Coupon as 쿠폰
    participant UserPoint as 사용자 포인트
    participant Order as 주문
    participant DataPlatform as 데이터플랫폼

    User ->>+ Payment: 결제 요청 (orderId, couponId)

    Payment ->>+ Coupon: 쿠폰 사용 요청
    Note over Coupon: 쿠폰 유효성 검증
    
    alt 검증 실패
        Coupon -->> Payment: 쿠폰 사용 실패
        Payment -->> User: 결제 실패 (쿠폰)
    else 검증 성공
        Coupon -->>- Payment: 쿠폰 사용 성공 (할인 금액 반환)

        Payment ->>+ UserPoint: 잔액 확인 요청(userId)

        alt 잔액 부족
            UserPoint -->> Payment: 잔액 부족 응답
            Payment -->> User: 결제 실패 (잔액)
        else 잔액 충분
            UserPoint ->> UserPoint: 잔액 차감 처리
            UserPoint -->>- Payment: 잔액 차감 완료

            Payment ->>+ Order: 주문 상태 업데이트 요청
            Order -->>- Payment: 주문 상태 업데이트 완료

            Payment --) DataPlatform: 주문 데이터 저장
            Payment -->>- User: 결제 성공 응답
        end
    end
```

## 4. 선착순 쿠폰
### 선착순 쿠폰 발급
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Coupon as 쿠폰

    User ->>+ Coupon: 선착순 쿠폰 발급 요청(userId, couponId)
    Note over Coupon: 쿠폰 유효성 검증

    alt 검증 실패
        Coupon -->> User: 쿠폰 발급 실패
    else 검증 성공
        Coupon -->>- User: 쿠폰 발급 성공
    end
```
### 보유 쿠폰 목록 조회 
```mermaid
sequenceDiagram
  actor User as 사용자
  participant Coupon as 쿠폰
  
  User ->>+ Coupon: 쿠폰 조회 요청(userId)
  note over Coupon: 쿠폰 유효성 검증
  
  alt 검증 실패
    Coupon -->> User: 쿠폰 조회 실패
  else 검증 성공
    Coupon -->>- User: 쿠폰 조회 성공
  end
```
## 5. 인기 판매 상품
### 인기 판매 상품 조회
```mermaid
sequenceDiagram
    actor User as 사용자
    participant ProductSalesRank as 인기 상품
    
    User ->>+ ProductSalesRank: 인기 상품 조회 요청
    ProductSalesRank ->> ProductSalesRank: 인기 상품 조회
    ProductSalesRank -->>- User: 인기 상품 목록 반환
```

### 인기 판매 상품 생성
```mermaid
sequenceDiagram
    participant Scheduler as 스케쥴러
    participant Order as 주문
    participant ProductSalesRank as 인기 상품
    
	loop 특정시간 매 1회 실행 
		Scheduler ->>+ ProductSalesRank: 인기 상품 데이터 생성 요청
        ProductSalesRank ->>+ Order: 최근 3일 기준 판매량 상위 5개 조회
		Order -->>- ProductSalesRank: 판매량 상위 5개 상품 반환
        ProductSalesRank ->> ProductSalesRank: 인기 상품 목록 저장
        ProductSalesRank -->>- Scheduler: 스케쥴러 작업 완료 
	end
```
