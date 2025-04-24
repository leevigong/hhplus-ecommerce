package kr.hhplus.be.server.support.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus httpStatus,
        String errorCode,
        String message
) {
}
