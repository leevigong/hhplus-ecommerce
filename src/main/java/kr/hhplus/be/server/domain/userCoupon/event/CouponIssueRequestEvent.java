package kr.hhplus.be.server.domain.userCoupon.event;

public record CouponIssueRequestEvent(
        Long couponId,
        Long userId
) {
}
