package kr.hhplus.be.server.domain.coupon;

import java.util.Collection;
import java.util.Set;

public interface CouponRequestRepository {

    boolean enqueueCouponCandidate(long couponId, long userId);

    Set<Long> fetchCouponCandidates(long couponId, int limit);

    void removeCouponCandidates(long couponId, Collection<? extends Long> userIds);
}
