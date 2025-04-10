package kr.hhplus.be.server.inferfaces.order.dto;

import kr.hhplus.be.server.application.order.OrderCriteria;

import java.util.List;
import java.util.stream.Collectors;

public record OrderCreateRequest(
        Long userId,
        List<OrderProductRequest> orderProducts,
        Long userCouponId
) {

    public OrderCriteria.Order toCriteria() {
        List<OrderCriteria.OrderItem> items = this.orderProducts.stream()
                .map(p -> new OrderCriteria.OrderItem(p.productId(), p.quantity()))
                .collect(Collectors.toList());

        return new OrderCriteria.Order(this.userId, items, this.userCouponId);
    }

    public record OrderProductRequest(
            Long productId,
            int quantity
    ) {
    }
}
