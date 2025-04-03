package kr.hhplus.be.server.global;

public record ErrorResponse(
        String errorCode,
        String message
) {
}
