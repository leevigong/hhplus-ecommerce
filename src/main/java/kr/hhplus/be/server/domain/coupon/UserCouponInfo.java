package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

public record UserCouponInfo(
        Long userCouponId,
        Long couponId,
        String couponCode,
        UserCouponStatus userCouponStatus,
        DiscountType discountType,
        long discountAmount,
        LocalDateTime expiredAt,
        LocalDateTime createdAt
) {
    public static UserCouponInfo from(UserCoupon userCoupon) {
        return new UserCouponInfo(
                userCoupon.getId(),
                userCoupon.getCoupon().getId(),
                userCoupon.getCoupon().getCouponCode(),
                userCoupon.getUserCouponStatus(),
                userCoupon.getCoupon().getDiscountType(),
                userCoupon.getCoupon().getDiscountAmount(),
                userCoupon.getCoupon().getExpiredAt(),
                userCoupon.getCreatedAt()
        );
    }
}
