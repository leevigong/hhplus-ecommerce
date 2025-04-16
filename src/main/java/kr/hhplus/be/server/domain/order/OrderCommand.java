package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

public class OrderCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private Long userId;
        private Long userCouponId;
        private List<CreateOrderItem> createOrderItems;

        public List<OrderItem> toOrderItems() {
            if (createOrderItems == null) {
                return List.of();
            }
            return createOrderItems.stream()
                    .map(CreateOrderItem::toOrderItem)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderItem {
        private Long productId;
        private int quantity;
        private long price;

        public OrderItem toOrderItem() {
            return OrderItem.builder()
                    .productId(this.productId)
                    .quantity(this.quantity)
                    .price(this.price)
                    .build();
        }
    }

    public record Confirm(Long orderId) {
        public static Confirm from(Long orderId) {
            return new Confirm(orderId);
        }
    }

    public record ApplyCoupon(Long orderId, Long userCouponId) {
        public static ApplyCoupon of(Long orderId, Long userCouponId) {
            return new ApplyCoupon(orderId, userCouponId);
        }
    }
}
