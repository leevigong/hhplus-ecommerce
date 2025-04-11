package kr.hhplus.be.server.global.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus httpStatus,
        String errorCode,
        String message
) {
}
