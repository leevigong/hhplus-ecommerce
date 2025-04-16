package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final UserCouponJpaRepository userCouponJpaRepository;

    public UserCouponRepositoryImpl(UserCouponJpaRepository userCouponJpaRepository) {
        this.userCouponJpaRepository = userCouponJpaRepository;
    }

    @Override
    public UserCoupon findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_USER_COUPON));
    }

    @Override
    public List<UserCoupon> findByUserId(Long userId) {
        return userCouponJpaRepository.findByUserId(userId);
    }

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }
}
