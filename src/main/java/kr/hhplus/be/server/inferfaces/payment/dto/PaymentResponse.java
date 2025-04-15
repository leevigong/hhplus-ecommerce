package kr.hhplus.be.server.inferfaces.payment.dto;

import kr.hhplus.be.server.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        Long userId,
        long amount,
        LocalDateTime createdAt,
        OrderStatus orderStatus
) {
}
