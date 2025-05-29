package kr.hhplus.be.server.application.coupon.event;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.userCoupon.UserCouponCommand;
import kr.hhplus.be.server.domain.userCoupon.UserCouponService;
import kr.hhplus.be.server.domain.userCoupon.event.CouponIssueRequestEvent;
import kr.hhplus.be.server.support.kafka.KafkaGroups;
import kr.hhplus.be.server.support.kafka.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class CouponIssueConsumer {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    public CouponIssueConsumer(CouponService couponService,
                               UserCouponService userCouponService) {
        this.couponService = couponService;
        this.userCouponService = userCouponService;
    }

    @KafkaListener(topics = KafkaTopics.COUPON_ISSUE_REQUESTED, groupId = KafkaGroups.COUPON_CONSUMER)
    @Transactional
    public void listen(CouponIssueRequestEvent event, Acknowledgment ack) {
        try {
            CouponInfo.Issue issued = couponService.issueCoupon(CouponCommand.Issue.of(event.couponId(), event.userId()));
            userCouponService.createUserCoupon(UserCouponCommand.of(issued.getCoupon(), event.userId()));

            ack.acknowledge();
        } catch (Exception ex) {
            log.error("COUPON_ISSUE_REQUESTED 에러 발생: {}", ex.getMessage());
        }
    }
}
