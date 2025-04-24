package kr.hhplus.be.server.domain.userCoupon;

public record UserCouponInfo(
        Long userCouponId
) {
    public static UserCouponInfo from(UserCoupon userCoupon) {
        return new UserCouponInfo(userCoupon.getId());
    }
}
