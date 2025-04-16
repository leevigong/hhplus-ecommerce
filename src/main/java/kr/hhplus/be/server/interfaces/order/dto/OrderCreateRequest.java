package kr.hhplus.be.server.interfaces.order.dto;

import kr.hhplus.be.server.application.order.OrderCriteria;

import java.util.List;
import java.util.stream.Collectors;

public record OrderCreateRequest(
        Long userId,
        List<OrderItemRequest> orderItems,
        Long userCouponId
) {

    public OrderCriteria.Create toCriteria() {
        List<OrderCriteria.OrderItem> items = this.orderItems.stream()
                .map(item -> new OrderCriteria.OrderItem(item.productId(), item.quantity(), item.price()))
                .collect(Collectors.toList());

        return new OrderCriteria.Create(this.userId, items, this.userCouponId);
    }

    public record OrderItemRequest(
            Long productId,
            int quantity,
            long price
    ) {
    }
}
