package kr.hhplus.be.server.domain.userCoupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {

    UserCoupon getById(Long userCouponId);

    List<UserCoupon> findByUserId(Long userId);

    UserCoupon save(UserCoupon userCoupon);

    Optional<UserCoupon> findByCouponIdAndUserId(Long couponId, Long userId);
}
