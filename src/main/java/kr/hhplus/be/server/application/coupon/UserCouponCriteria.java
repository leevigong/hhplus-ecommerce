package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;

public record UserCouponCriteria(
        Long couponId,
        Long userId
) {

    public CouponCommand toCommand(Long couponId) {
        return new CouponCommand(couponId);
    }

    public static UserCouponCriteria of(Long couponId, Long userId) {
        return new UserCouponCriteria(couponId, userId);
    }
}
