package kr.hhplus.be.server.inferfaces.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "사용자 쿠폰 응답")
public record UserCouponResponse(
        Long couponId,
        String couponCode,
        CouponStatus couponStatus,
        DiscountType discountType,
        BigDecimal discountAmount,
        LocalDateTime createdAt,
        LocalDateTime usedAt,
        LocalDateTime expiredAt
) {
}

