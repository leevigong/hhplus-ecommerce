package kr.hhplus.be.server.domain.userCoupon;

import java.util.List;

public interface UserCouponRepository {

    UserCoupon getById(Long userCouponId);

    List<UserCoupon> findByUserId(Long userId);

    UserCoupon save(UserCoupon userCoupon);
}
