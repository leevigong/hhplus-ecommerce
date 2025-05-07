package kr.hhplus.be.server.application.coupon;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponResult {

    @Getter
    public static class Coupons {

        private final List<Long> userCouponId;

        private Coupons(List<Long> userCouponId) {
            this.userCouponId = userCouponId;
        }

        public static Coupons of(List<Long> userCouponId) {
            return new Coupons(userCouponId);
        }
    }
}
