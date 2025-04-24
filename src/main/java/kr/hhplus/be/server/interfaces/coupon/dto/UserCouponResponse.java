package kr.hhplus.be.server.interfaces.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.userCoupon.UserCouponInfo;

@Schema(description = "사용자 쿠폰 응답")
public record UserCouponResponse(
        Long couponId
) {

    public static UserCouponResponse from(UserCouponInfo userCouponInfo) {
        return new UserCouponResponse(userCouponInfo.userCouponId());
    }
}

