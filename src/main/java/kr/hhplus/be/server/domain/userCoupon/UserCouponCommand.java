package kr.hhplus.be.server.domain.userCoupon;

import kr.hhplus.be.server.domain.coupon.Coupon;

public record UserCouponCommand(
        Coupon coupon,
        Long userId
) {

    public static UserCouponCommand of(Coupon coupon, Long userId) {
        return new UserCouponCommand(coupon, userId);
    }
}
