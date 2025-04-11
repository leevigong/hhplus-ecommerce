package kr.hhplus.be.server.inferfaces.coupon.dto;

import kr.hhplus.be.server.domain.coupon.DiscountType;
import kr.hhplus.be.server.domain.coupon.UserCouponInfo;
import kr.hhplus.be.server.domain.coupon.UserCouponStatus;

import java.time.LocalDateTime;

public record CouponIssueResponse(
        Long couponId,
        String couponCode,
        UserCouponStatus couponStatus,
        DiscountType discountType,
        long discountAmount,
        LocalDateTime createdAt
) {
    public static CouponIssueResponse from(UserCouponInfo userCouponInfo) {
        return new CouponIssueResponse(
                userCouponInfo.couponId(),
                userCouponInfo.couponCode(),
                userCouponInfo.userCouponStatus(),
                userCouponInfo.discountType(),
                userCouponInfo.discountAmount(),
                userCouponInfo.createdAt()
        );
    }
}
