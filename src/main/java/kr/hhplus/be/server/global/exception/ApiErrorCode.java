package kr.hhplus.be.server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // User
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "유효하지 않은 닉네임입니다."),

    // UserBalance
    NEGATIVE_BALANCE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "잔액은 음수일 수 없습니다."),
    INVALID_CHARGE_MIN_AMOUNT(HttpStatus.BAD_REQUEST, "충전 금액은 최소 100원 이상이어야 합니다."),
    INVALID_CHARGE_MAX_AMOUNT(HttpStatus.BAD_REQUEST, "충전 금액은 최대 1,000,000원을 초과할 수 없습니다."),
    INVALID_USE_MIN_AMOUNT(HttpStatus.BAD_REQUEST, "1원 이상 사용해야 합니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),

    // UserBalanceHistory
    NEGATIVE_BALANCE_HISTORY_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "음수일 수 없습니다."),

    // Product
    NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "해당 상품을 찾을 수 없습니다."),
    INVALID_PRODUCT_NAME(HttpStatus.BAD_REQUEST, "상품 이름은 필수입니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_STOCK_INCREASE_AMOUNT(HttpStatus.BAD_REQUEST, "증가 수량은 0 이상이어야 합니다."),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "상품 가격은 0보다 커야 합니다."),
    MISSING_PRODUCT_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리는 필수입니다."),
    INVALID_RANKING_SCOPE(HttpStatus.BAD_REQUEST, "유효하지 않은 랭킹 기준(범위)입니다."),

    // Coupon
    NOT_FOUND_COUPON(HttpStatus.BAD_REQUEST, "해당 쿠폰을 찾을 수 없습니다."),
    NOT_FOUND_USER_COUPON(HttpStatus.BAD_REQUEST, "해당 유저 쿠폰을 찾을 수 없습니다."),
    COUPON_NOT_AVAILABLE_TO_ISSUE(HttpStatus.BAD_REQUEST, "해당 쿠폰은 더 이상 발급할 수 없습니다."),
    INVALID_COUPON_STATUS(HttpStatus.BAD_REQUEST, "잘못된 쿠폰 상태입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "쿠폰이 만료되어 복구할 수 없습니다."),
    ALREADY_COUPON_APPLIED(HttpStatus.BAD_REQUEST, "이미 쿠폰이 적용된 주문입니다."),
    INVALID_DISCOUNT_AMOUNT(HttpStatus.BAD_REQUEST, "할인 금액이 주문 금액을 초과할 수 없습니다."),
    USER_COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "쿠폰이 만료되어 사용할 수 없습니다."),

    // Order
    INVALID_USER(HttpStatus.BAD_REQUEST, "유효하지 않은 유저입니다."),
    EMPTY_ORDER_ITEMS(HttpStatus.BAD_REQUEST, "주문 항목은 최소 하나 이상이어야 합니다."),
    NOT_FOUND_ORDER(HttpStatus.BAD_REQUEST, "해당 주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "잘못된 주문 상태입니다."),

    // Payment
    NOT_FOUND_PAYMENT(HttpStatus.BAD_REQUEST, "해당 결제를 찾을 수 없습니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액은 음수일 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ApiErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode() {
        return httpStatus.value();
    }
}

