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
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_STOCK_INCREASE_AMOUNT(HttpStatus.BAD_REQUEST, "증가 수량은 0 이상이어야 합니다."),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "상품 가격은 0보다 커야 합니다."),
    MISSING_PRODUCT_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리는 필수입니다."),
    INVALID_RANKING_SCOPE(HttpStatus.BAD_REQUEST, "유효하지 않은 랭킹 기준(범위)입니다.");


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

