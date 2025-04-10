package kr.hhplus.be.server.domain.coupon;

public record UserCouponCommand(
        Long couponId,
        Long userId
) {

    public static UserCouponCommand of(Long couponId, Long userId) {
        return new UserCouponCommand(couponId, userId);
    }
}
