package kr.hhplus.be.server.inferfaces.coupon.dto;

import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponIssueResponse(
        Long couponId,
        String couponCode,
        CouponStatus couponStatus,
        DiscountType discountType,
        BigDecimal discountAmount,
        LocalDateTime expiredAt,
        LocalDateTime createdAt
) {
}
