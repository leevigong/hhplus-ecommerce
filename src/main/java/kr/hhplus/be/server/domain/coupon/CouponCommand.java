package kr.hhplus.be.server.domain.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CouponCommand {

    @Getter
    @NoArgsConstructor
    public static class Issue {

        private Long couponId;
        private Long userId;

        public Issue(Long couponId, Long userId) {
            this.couponId = couponId;
            this.userId = userId;
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
    }

    @Getter
    @NoArgsConstructor
    public static class Publish {

        private Coupon coupon;
        private int limit;

        public Publish(Coupon coupon, int limit) {
            this.coupon = coupon;
            this.limit = limit;
        }

        public static Publish of(Coupon coupon, int limit) {
            return new Publish(coupon, limit);
        }
    }
}
