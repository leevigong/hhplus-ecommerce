package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.userCoupon.UserCouponCommand;
import kr.hhplus.be.server.domain.userCoupon.UserCouponInfo;
import kr.hhplus.be.server.domain.userCoupon.UserCouponService;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserCouponFacade {

    private final CouponService couponService;
    private final UserCouponService userCouponService;
    private final RedissonClient redisson;

    public UserCouponFacade(CouponService couponService, UserCouponService userCouponService, RedissonClient redisson, TransactionTemplate tx) {
        this.couponService = couponService;
        this.userCouponService = userCouponService;
        this.redisson = redisson;
    }

    @Transactional(readOnly = true)
    public UserCouponResult.Coupons getUserCoupons(Long userId) {
        List<UserCouponInfo> userCouponInfos = userCouponService.getUserCoupons(userId);
        List<Long> userCouponIds = userCouponInfos.stream()
                .map(UserCouponInfo::userCouponId)
                .collect(Collectors.toList());
        return UserCouponResult.Coupons.of(userCouponIds);
    }

    public void issue(UserCouponCriteria criteria) {
        RLock lock = redisson.getLock("userCoupon:issue:" + criteria.couponId());
        log.info("lock: {}", lock.getName());

        boolean acquired = false;
        try {
            acquired = lock.tryLock(10, 20, TimeUnit.SECONDS);
            if (!acquired) {
                throw new ApiException(ApiErrorCode.COUPON_ISSUE_IN_PROGRESS);
            }

            // 쿠폰 발급
            CouponInfo couponInfo = couponService.issueCoupon(criteria.toCommand());

            // 사용자 쿠폰 생성
            userCouponService.createUserCoupon(UserCouponCommand.of(couponInfo.coupon(), criteria.userId()));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) lock.unlock();
        }
    }
}
