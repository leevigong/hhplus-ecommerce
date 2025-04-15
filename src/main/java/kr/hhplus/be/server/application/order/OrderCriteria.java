package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderCommand;

import java.util.List;

public class OrderCriteria {

    public record Create(
            Long userId,
            List<OrderItem> orderItems,
            Long couponIssueId
    ) {
        public OrderCommand.Create toCommand() {
            return new OrderCommand.Create(
                    this.userId,
                    this.couponIssueId,
                    this.orderItems().stream()
                            .map(item -> new OrderCommand.CreateOrderItem(item.productId(), item.quantity(), item.price))
                            .toList()
            );
        }
    }

    public record OrderItem(
            Long productId,
            int quantity,
            long price
    ) {
    }
}
