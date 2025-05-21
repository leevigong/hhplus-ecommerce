package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class OrderCriteria {

    @Getter
    public static class Create {
        private final Long userId;
        private final List<OrderItem> orderItems;
        private final Long couponIssueId;

        private Create(Long userId, List<OrderItem> orderItems, Long couponIssueId) {
            this.userId = userId;
            this.orderItems = orderItems;
            this.couponIssueId = couponIssueId;
        }

        public static Create of(Long userId, List<OrderItem> orderItems, Long couponIssueId) {
            return new Create(userId, orderItems, couponIssueId);
        }

        public OrderCommand.Create toCommand() {
            return new OrderCommand.Create(
                    this.userId,
                    this.couponIssueId,
                    this.orderItems.stream()
                            .map(item -> new OrderCommand.CreateOrderItem(item.getProductId(), item.getQuantity(), item.getPrice()))
                            .toList()
            );
        }
    }

    @Getter
    public static class OrderItem {
        private final Long productId;
        private final int quantity;
        private final long price;

        private OrderItem(Long productId, int quantity, long price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        public static OrderItem of(Long productId, int quantity, long price) {
            return new OrderItem(productId, quantity, price);
        }
    }
}
