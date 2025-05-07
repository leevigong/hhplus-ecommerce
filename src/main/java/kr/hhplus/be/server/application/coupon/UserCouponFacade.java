package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.userCoupon.UserCouponCommand;
import kr.hhplus.be.server.domain.userCoupon.UserCouponService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserCouponFacade {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    public UserCouponFacade(CouponService couponService, UserCouponService userCouponService) {
        this.couponService = couponService;
        this.userCouponService = userCouponService;
    }

    @Transactional
    public void issue(UserCouponCriteria criteria) {
        // 쿠폰 발급
        CouponInfo couponInfo = couponService.issueCoupon(criteria.toCommand());

        // 사용자 쿠폰 생성
        userCouponService.createUserCoupon(UserCouponCommand.of(couponInfo.coupon(), criteria.userId()));
    }
}
