package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;

public record OrderInfo(
        Long orderId,
        String status,
        long totalPrice,
        long discountPrice,
        long finalPrice,
        LocalDateTime createdAt
) {
    public static OrderInfo from(Order order) {
        return new OrderInfo(
                order.getId(),
                order.getOrderStatus().name(),
                order.getTotalPrice(),
                order.getDiscountPrice(),
                order.getFinalPrice(),
                order.getCreatedAt()
        );
    }
}
