package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderInfo(
        Long orderId,
        String status,
        long totalPrice,
        long discountPrice,
        long finalPrice,
        LocalDateTime createdAt,
        List<OrderItem> orderItems
) {
    public static OrderInfo from(Order order) {
        return new OrderInfo(
                order.getId(),
                order.getOrderStatus().name(),
                order.getTotalPrice(),
                order.getDiscountPrice(),
                order.getFinalPrice(),
                order.getCreatedAt(),
                order.getOrderItems()
        );
    }
}
