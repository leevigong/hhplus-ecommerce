package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderInfo;

import java.time.LocalDateTime;

public record OrderResult(
        Long orderId,
        String status,
        long totalPrice,
        long discountAmount,
        long finalPrice,
        LocalDateTime createdAt
) {

    public static OrderResult from(OrderInfo orderInfo) {
        return new OrderResult(
                orderInfo.orderId(),
                orderInfo.status(),
                orderInfo.totalPrice(),
                orderInfo.discountAmount(),
                orderInfo.finalPrice(),
                orderInfo.createdAt()
        );
    }
}
