package kr.hhplus.be.server.inferfaces.payment.dto;

import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.payment.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        Long userId,
        PaymentType paymentType,
        BigDecimal amount,
        String receipt,
        LocalDateTime createdAt,
        OrderStatus orderStatus
) {
}
