package kr.hhplus.be.server.interfaces.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.payment.PaymentCriteria;

public record PaymentRequest(
        @Schema(description = "주문 ID", example = "123")
        Long orderId,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "결제 금액", example = "49900.00")
        long amount
) {

    public PaymentCriteria toCriteria() {
        return new PaymentCriteria(userId, orderId, amount);
    }
}

