package kr.hhplus.be.server.support.exception;

public class ApiException extends RuntimeException {

    private final ApiErrorCode errorCode;

    public ApiException(ApiErrorCode apiErrorCode) {
        super(apiErrorCode.getMessage());
        this.errorCode = apiErrorCode;
    }

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }
}
