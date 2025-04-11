package kr.hhplus.be.server.inferfaces.order.dto;

import kr.hhplus.be.server.application.order.OrderResult;

import java.time.LocalDateTime;

public record OrderResponse(
        long orderId,
        long finalPrice,
        LocalDateTime orderedAt
) {

    public static OrderResponse from(OrderResult result) {
        return new OrderResponse(result.orderId(), result.finalPrice(), result.createdAt());
    }
}
