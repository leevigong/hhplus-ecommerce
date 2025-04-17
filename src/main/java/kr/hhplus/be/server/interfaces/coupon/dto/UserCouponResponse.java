package kr.hhplus.be.server.interfaces.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import kr.hhplus.be.server.domain.coupon.UserCouponInfo;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;

import java.time.LocalDateTime;

@Schema(description = "사용자 쿠폰 응답")
public record UserCouponResponse(
        Long couponId,
        String couponCode,
        UserCouponStatus couponStatus,
        DiscountType discountType,
        long discountAmount,
        LocalDateTime createdAt
) {

    public static UserCouponResponse from(UserCouponInfo userCouponInfo) {
        return new UserCouponResponse(
                userCouponInfo.couponId(),
                userCouponInfo.couponCode(),
                userCouponInfo.userCouponStatus(),
                userCouponInfo.discountType(),
                userCouponInfo.discountAmount(),
                userCouponInfo.createdAt()
        );
    }
}

