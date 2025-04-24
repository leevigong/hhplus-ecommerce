package kr.hhplus.be.server.domain.coupon;

public record CouponCommand(
        Long couponId
) {

    public static CouponCommand of(Long couponId) {
        return new CouponCommand(couponId);
    }
}
