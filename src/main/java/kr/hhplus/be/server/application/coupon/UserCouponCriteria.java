package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserCouponCriteria {

    @Getter
    @NoArgsConstructor
    public static class Issue {

        private Long couponId;
        private Long userId;

        public Issue(Long couponId, Long userId) {
            this.couponId = couponId;
            this.userId = userId;
        }

        public CouponCommand.Issue toCommand() {
            return new CouponCommand.Issue(couponId, userId);
        }

        public static Issue of(Long couponId, Long userId) {
            return new Issue(couponId, userId);
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

        public CouponCommand.PublishRequest toCommand() {
            return new CouponCommand.PublishRequest(couponId, userId);
        }

        public static PublishRequest of(Long couponId, Long userId) {
            return new PublishRequest(couponId, userId);
        }
    }
}
