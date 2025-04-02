## ERD(Entity Relationship Diagram)

```mermaid
erDiagram
    user {
        BIGINT id PK "유저 ID"
        VARCHAR name "이름"
        VARCHAR email "이메일"
        VARCHAR password "비밀번호"
        DATETIME created_at "생성 일시"
        DATETIME updated_at "수정 일시"
    }

    user_point {
        BIGINT user_id PK "사용자 ID"
        DECIMAL balance "현재 잔액"
        DATETIME created_at "생성 일시"
        DATETIME updated_at "수정 일시"
    }

    point_history {
        BIGINT id PK "포인트 내역 ID"
        BIGINT user_id FK "사용자 ID"
        ENUM transaction_type "타입 [CHARGE, USE]"
        DECIMAL amount "변동 금액"
        DECIMAL before_balance "변동 전 잔액"
        DECIMAL after_balance "변동 후 잔액"
        DATETIME created_at "생성 일시"
        DATETIME updated_at "갱신 일시"
    }

    product {
        BIGINT id PK "상품 ID"
        VARCHAR name "상품 이름"
        INT price "상품 가격"
        INT stock_quantity "상품 재고"
        ENUM category "상품 카테고리 [OUTER, TOP, BOTTOM ..]"
        DATETIME created_at "생성 일시"
        DATETIME updated_at "수정 일시"
    }

    product_sales_rank {
        BIGINT id PK "인기 상품 ID"
        BIGINT product_id FK "상품 ID"
        INT sales_count "기간내 판매 수량"
        DOUBLE total_sales_price "기간내 총 판매 가격"
        ENUM ranking_scope "랭킹 기준 범위 [3DAYS, 7DAYS]"
        INT rank_position "이번 집계 랭킹 순위"
        DATETIME created_at "생성 일시"
        DATETIME updated_at "수정 일시"
    }

    order {
        BIGINT id PK "주문 ID"
        BIGINT user_id FK "사용자 ID"
        BIGINT user_coupon_id FK "사용자 쿠폰 ID"
        BIGINT order_item_id FK "주문 아이템 ID"
        DOUBLE total_price "주문 총액"
        DOUBLE discount_amount "할인 금액"
        DOUBLE final_price "최종 주문 가격"
        ENUM order_status "주문 상태 [PENDING, PAID, CANCELLED, FAILED]"
        DATETIME created_at "생성(주문) 일시"
        DATETIME updated_at "수정 일시"
    }

    order_item {
        BIGINT id PK "주문 아이템 ID"
        BIGINT product_id FK "상품 ID"
        INT quantity "주문 수량"
        DATETIME created_at "생성 일시"
        DATETIME updated_at "수정 일시"
    }

    payment {
        BIGINT id PK "결제 ID"
        BIGINT order_id FK "주문 ID"
        BIGINT user_id FK "사용자 ID"
        ENUM payment_type "결제 수단"
        DOUBLE amount "결제 금액"
        VARCHAR receipt "거래 영수증"
        DATETIME created_at "생성(결제) 일시"
        DATETIME updated_at "수정 일시"
    }

    coupon {
        BIGINT id PK "쿠폰 ID"
        VARCHAR coupon_code "쿠폰코드"
        ENUM discount_type "할인 타입 [AMOUNT, RATE]"
        DOUBLE discount_amount "할인 가격"
        ENUM coupon_status "쿠폰 상태 [ACTIVE, INACTIVE]"
        INT max_issued_quantity "최대 발급 가능 수량"
        INT issued_quantity "현재까지 발급된 수량"
        DATETIME expires_at "만료 일시"
        DATETIME created_at "생성 일시"
        DATETIME updated_at "수정 일시"
    }

    user_coupon {
        BIGINT id PK "사용자 쿠폰 ID"
        BIGINT user_id FK "사용자 ID"
        BIGINT coupon_id FK "쿠폰 ID"
        ENUM user_coupon_status "쿠폰 상태 [AVAILABLE, USED, EXPIRED]"
        DATETIME created_at "생성(발급) 일시"
        DATETIME updated_at "수정(사용) 일시"
    }

    user ||--|| user_point: owns_point
    user ||--o{ point_history: has_point_history
    user ||--o{ payment: pay
    user ||--o{ order: places_order
    user ||--o{ user_coupon: owns_user_coupon
    order ||--o{ order_item: includes_items
    order ||--|| payment: has_payment
    product ||--o{ order_item: part_of_order
    product ||--|| product_sales_rank: has_sales_rank
    coupon ||--o{ user_coupon: issued_to_user
    user_coupon ||--|| order: used_in_order

```



---

## 엔티티 관계 정리

#### 1. user <-> user_point (1:1)
사용자는 하나의 포인트 계좌를 가진다.

#### 2. user <-> point_history (1:N)
사용자는 여러 개의 포인트 거래 내역을 가질 수 있다.

#### 3. user <-> payment (1:N)
사용자는 여러 개의 결제 정보를 가질 수 있다.

#### 4. user <-> order (1:N)
사용자는 여러 개의 주문을 생성할 수 있다.

#### 5. user <-> user_coupon (1:N)
사용자는 여러 개의 쿠폰을 발급받을 수 있다.

#### 6. coupon <-> user_coupon (1:N)
하나의 쿠폰은 여러 사용자에게 발급될 수 있다.

#### 7. user_coupon <-> order (1:1)
하나의 사용자 쿠폰은 하나의 주문에만 사용될 수 있다.  
주문당 하나의 쿠폰만 적용 가능하며, 쿠폰은 한 번 사용되면 더 이상 사용할 수 없다.

#### 8. order <-> order_item (1:N)
하나의 주문은 여러 개의 주문 아이템을 포함할 수 있다.

#### 9. order <-> payment (1:1)
하나의 주문에는 하나의 결제만 매핑된다.

#### 10. product <-> order_item (1:N)
하나의 상품은 여러 주문 아이템에 포함될 수 있다.

#### 11. product <-> product_sales_rank (1:1)
하나의 상품은 하나의 판매 랭킹 정보를 가진다.  
인기 상품 조회 시, 랭킹 정보와 함께 상품 정보를 조회하기 위해 1:1로 연결된다.
