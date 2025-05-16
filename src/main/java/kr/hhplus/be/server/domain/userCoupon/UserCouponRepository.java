package kr.hhplus.be.server.domain.userCoupon;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserCouponRepository {

    UserCoupon getById(Long userCouponId);

    List<UserCoupon> findByUserId(Long userId);

    UserCoupon save(UserCoupon userCoupon);

    Optional<UserCoupon> findByCouponIdAndUserId(Long couponId, Long userId);

    boolean enqueueCouponCandidate(long couponId, long userId);

    boolean existsByCouponIdAndUserId(long couponId, Long userId);

    Set<Long> fetchCouponCandidates(long couponId, int limit);

    void removeCouponCandidates(long couponId, Collection<Long> userIds);
}
