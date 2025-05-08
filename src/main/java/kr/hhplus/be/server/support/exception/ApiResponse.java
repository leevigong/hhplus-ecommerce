package kr.hhplus.be.server.support.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @param isSuccess 요청 성공 여부
 * @param code      HTTPS 코드
 * @param data      응답 데이터
 * @param message   에러 메시지
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean isSuccess,
        int code,
        T data,
        String message
) {

    // 성공 응답 (message는 null)
    public static <T> ApiResponse<T> success(int code, T data) {
        return new ApiResponse<>(true, code, data, null);
    }

    // 실패 응답 (data는 null)
    public static <T> ApiResponse<T> failure(int code, String message) {
        return new ApiResponse<>(false, code, null, message);
    }
}
