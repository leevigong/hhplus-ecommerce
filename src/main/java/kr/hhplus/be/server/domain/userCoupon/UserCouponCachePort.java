package kr.hhplus.be.server.domain.userCoupon;

import java.util.Collection;
import java.util.Set;

public interface UserCouponCachePort {

    boolean enqueueCouponCandidate(long couponId, long userId);

    Set<Long> fetchCouponCandidates(long couponId, int limit);

    void removeCouponCandidates(long couponId, Collection<? extends Long> userIds);
}
