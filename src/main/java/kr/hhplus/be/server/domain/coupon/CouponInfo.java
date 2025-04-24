package kr.hhplus.be.server.domain.coupon;

public record CouponInfo(
        Coupon coupon
) {
    public static CouponInfo from(Coupon coupon) {
        return new CouponInfo(coupon);
    }
}
