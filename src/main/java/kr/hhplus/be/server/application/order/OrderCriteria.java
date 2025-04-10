package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderCommand;

import java.util.List;
import java.util.stream.Collectors;

public class OrderCriteria {

    public record Order(Long userId, List<OrderItem> orderItems, Long couponIssueId) {

        public OrderCommand.Order toCommand() {
            return new OrderCommand.Order(
                    this.userId,
                    this.couponIssueId,
                    this.orderItems().stream()
                            .map(item -> new OrderCommand.OrderItem(item.productId(), item.quantity()))
                            .toList()
            );
        }

        public static Order fromCommand(OrderCommand.Order command) {
            List<OrderItem> items = command.getOrderItems().stream()
                    .map(item -> new OrderItem(item.getProductId(), item.getQuantity()))
                    .collect(Collectors.toList());

            return new Order(command.getUserId(), items, command.getUserCouponId());
        }
    }

    public record OrderItem(Long productId, int quantity) {
    }
}
