package kr.hhplus.be.server.domain.userCoupon.event;

import kr.hhplus.be.server.support.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CouponIssueProducer {

    private final KafkaTemplate<String, Object> kafka;

    public CouponIssueProducer(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    public void send(Long couponId, Long userId) {
        kafka.send(KafkaTopics.COUPON_ISSUE_REQUESTED, String.valueOf(couponId), new CouponIssueRequestEvent(couponId, userId));
    }
}
