package kr.hhplus.be.server.interfaces.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.coupon.UserCouponCriteria;

public record UserCouponRequest(
        @Schema(description = "쿠폰 ID", example = "123")
        Long couponId,

        @Schema(description = "사용자 ID", example = "1")
        Long userId
) {

    public UserCouponCriteria toCriteria() {
        return new UserCouponCriteria(couponId, userId);
    }
}
