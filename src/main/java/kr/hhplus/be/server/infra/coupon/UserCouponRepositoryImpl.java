package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import kr.hhplus.be.server.domain.userCoupon.UserCouponRepository;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final UserCouponJpaRepository userCouponJpaRepository;
    private final UserCouponRedisCache userCouponRedisCache;

    public UserCouponRepositoryImpl(UserCouponJpaRepository userCouponJpaRepository,
                                    UserCouponRedisCache userCouponRedisCache) {
        this.userCouponJpaRepository = userCouponJpaRepository;
        this.userCouponRedisCache = userCouponRedisCache;
    }

    @Override
    public UserCoupon getById(Long userCouponId) {
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

    @Override
    public Optional<UserCoupon> findByCouponIdAndUserId(Long couponId, Long userId) {
        return userCouponJpaRepository.findByCouponIdAndUserId(couponId, userId);
    }

    @Override
    public boolean existsByCouponIdAndUserId(long couponId, Long userId) {
        return userCouponJpaRepository.existsByCouponIdAndUserId(couponId, userId);
    }

    /**
     * Redis 사용
     **/
    @Override
    public boolean enqueueCouponCandidate(long couponId, long userId) {
        return userCouponRedisCache.enqueueCouponCandidate(couponId, userId);
    }

    @Override
    public Set<Long> fetchCouponCandidates(long couponId, int limit) {
        return userCouponRedisCache.fetchCouponCandidates(couponId, limit);
    }

    @Override
    public void removeCouponCandidates(long couponId, Collection<Long> userIds) {
        userCouponRedisCache.removeCouponCandidates(couponId, userIds);
    }
}
