package kr.hhplus.be.server.domain.coupon;

import java.util.List;

public interface UserCouponRepository {

    UserCoupon findByCouponId(Long couponId);

    List<UserCoupon> findByUserId(Long userId);

    UserCoupon save(UserCoupon userCoupon);
}
