package kr.hhplus.be.server.domain.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CouponInfo {

    @Getter
    @NoArgsConstructor
    public static class Issue {

        private Coupon coupon;

        public Issue(Coupon coupon) {
            this.coupon = coupon;
        }

        public static Issue from(Coupon coupon) {
            return new Issue(coupon);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PublishableCoupon {

        private Coupon coupon;
        private int quantity;

        public PublishableCoupon(Coupon coupon, int quantity) {
            this.coupon = coupon;
            this.quantity = quantity;
        }

        public static PublishableCoupon of(Coupon coupon, int quantity) {
            return new PublishableCoupon(coupon, quantity);
        }
    }
}
