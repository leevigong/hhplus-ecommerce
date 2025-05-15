package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.userCoupon.UserCouponCommand;
import kr.hhplus.be.server.domain.userCoupon.UserCouponInfo;
import kr.hhplus.be.server.domain.userCoupon.UserCouponService;
import kr.hhplus.be.server.support.lock.DistributedLock;
import kr.hhplus.be.server.support.lock.LockResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCouponFacade {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    public UserCouponFacade(CouponService couponService, UserCouponService userCouponService) {
        this.couponService = couponService;
        this.userCouponService = userCouponService;
    }

    @Transactional(readOnly = true)
    public UserCouponResult.Coupons getUserCoupons(Long userId) {
        List<UserCouponInfo> userCouponInfos = userCouponService.getUserCoupons(userId);
        List<Long> userCouponIds = userCouponInfos.stream()
                .map(UserCouponInfo::userCouponId)
                .collect(Collectors.toList());
        return UserCouponResult.Coupons.of(userCouponIds);
    }

    @DistributedLock(resource = LockResource.USER_COUPON, key = "#criteria.couponId")
    @Transactional
    public void issue(UserCouponCriteria.Issue criteria) {
        // 쿠폰 발급
        CouponInfo.Issue couponInfo = couponService.issueCoupon(criteria.toCommand());

        // 사용자 쿠폰 생성
        userCouponService.createUserCoupon(UserCouponCommand.of(couponInfo.getCoupon(), criteria.getUserId()));
    }

    @Transactional
    public void requestPublishWithRedis(UserCouponCriteria.PublishRequest criteria) {
        userCouponService.requestPublishCoupon(criteria.toCommand());
    }

    @Transactional
    public void publishCouponCandidate() {
        List<CouponInfo.PublishableCoupon> publishableCoupons = couponService.getPublishableCoupons();
        publishableCoupons.forEach(
                pc ->
                        userCouponService.publishCouponCandidate(CouponCommand.Publish.of(pc.getCoupon(), pc.getQuantity()))
        );
    }
}
