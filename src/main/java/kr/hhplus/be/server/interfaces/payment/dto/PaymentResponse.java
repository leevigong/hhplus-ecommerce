package kr.hhplus.be.server.interfaces.payment.dto;

import kr.hhplus.be.server.domain.order.enums.OrderStatus;

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
