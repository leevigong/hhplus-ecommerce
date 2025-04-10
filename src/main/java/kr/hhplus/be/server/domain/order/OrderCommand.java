package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

public class OrderCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Order {
        private Long userId;
        private Long userCouponId;
        private List<OrderItem> orderItems;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private Long productId;
        private int quantity;
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
