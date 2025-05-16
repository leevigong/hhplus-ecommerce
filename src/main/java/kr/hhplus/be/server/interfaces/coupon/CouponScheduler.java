package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.UserCouponFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponScheduler {

    private final UserCouponFacade userCouponFacade;

    @Scheduled(cron = "0 0 0 * * *")
    public void publishUserCoupon() {
        log.info("선착순 쿠폰 발급 등록 스케줄러");
        try {
            userCouponFacade.publishCouponCandidate();
        } catch (Exception e) {
            log.error("선착순 쿠폰 발급 등록 스케줄러 실행 중 오류 발생", e);
        }
    }
}
