package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {

    @Override
    public UserCoupon findByCouponId(Long couponId) {
        return null;
    }

    @Override
    public List<UserCoupon> findByUserId(Long userId) {
        return List.of();
    }

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return null;
    }
}
