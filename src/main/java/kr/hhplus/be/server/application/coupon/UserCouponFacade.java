package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.userCoupon.UserCouponCommand;
import kr.hhplus.be.server.domain.userCoupon.UserCouponService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class UserCouponFacade {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    public UserCouponFacade(CouponService couponService, UserCouponService userCouponService) {
        this.couponService = couponService;
        this.userCouponService = userCouponService;
    }

    public void issue(UserCouponCriteria criteria) {
        CouponCommand command = criteria.toCommand(criteria.couponId());

        CouponInfo couponInfo = couponService.issueCoupon(command);

        UserCouponCommand userCouponCommand = UserCouponCommand.of(couponInfo.coupon(), criteria.userId());
        userCouponService.createUserCoupon(userCouponCommand);
    }
}
