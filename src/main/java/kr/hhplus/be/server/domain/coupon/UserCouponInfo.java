package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

public record UserCouponInfo(
        Long couponId,
        String couponCode,
        UserCouponStatus couponStatus,
        DiscountType discountType,
        long discountAmount,
        LocalDateTime createdAt
) {
    public static UserCouponInfo from(UserCoupon userCoupon) {
        return new UserCouponInfo(
                userCoupon.getCoupon().getId(),
                userCoupon.getCoupon().getCouponCode(),
                userCoupon.getCouponStatus(),
                userCoupon.getCoupon().getDiscountType(),
                userCoupon.getCoupon().getDiscountAmount(),
                userCoupon.getCreatedAt()
        );
    }
}
