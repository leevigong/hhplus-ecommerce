package kr.hhplus.be.server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // User
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "유효하지 않은 닉네임입니다."),
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

