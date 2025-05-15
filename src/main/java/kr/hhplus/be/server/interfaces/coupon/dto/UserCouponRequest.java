package kr.hhplus.be.server.interfaces.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.coupon.UserCouponCriteria;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserCouponRequest {

    @Getter
    @NoArgsConstructor
    public static class Issue {
        @Schema(description = "쿠폰 ID", example = "123")
        private Long couponId;

        @Schema(description = "사용자 ID", example = "1")
        private Long userId;

        public UserCouponCriteria.Issue toCriteria() {
            return new UserCouponCriteria.Issue(couponId, userId);
        }

    }

    @Getter
    @NoArgsConstructor
    public static class PublishRequest {

        private Long couponId;
        private Long userId;

        public PublishRequest(Long couponId, Long userId) {
            this.couponId = couponId;
            this.userId = userId;
        }

        public UserCouponCriteria.PublishRequest toCriteria() {
            return new UserCouponCriteria.PublishRequest(couponId, userId);
        }
    }
}
