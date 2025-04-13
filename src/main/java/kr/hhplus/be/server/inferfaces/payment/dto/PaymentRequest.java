package kr.hhplus.be.server.inferfaces.payment.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentRequest(
        @Schema(description = "주문 ID", example = "123")
        Long orderId,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "결제 금액", example = "49900.00")
        BigDecimal amount
) {
}

