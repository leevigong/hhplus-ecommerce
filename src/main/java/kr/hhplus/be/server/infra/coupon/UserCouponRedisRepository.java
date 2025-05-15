package kr.hhplus.be.server.infra.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserCouponRedisRepository {

    private static final String KEY_FORMAT = "coupon:%d:candidates";

    private final RedisTemplate<String, Long> redisTemplate;

    public boolean enqueueCouponCandidate(long couponId, long userId) {
        String key = keyOf(couponId);

        double score = (double) Instant.now().getEpochSecond();
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().addIfAbsent(key, userId, score));
    }

    public Set<Long> fetchCouponCandidates(long couponId, int limit) {
        if (limit <= 0) {
            return Set.of();
        }
        String key = keyOf(couponId);
        return redisTemplate.opsForZSet().range(key, 0, limit - 1);
    }

    public void removeCouponCandidates(long couponId, Collection<? extends Long> userIds) {
        String key = keyOf(couponId);
        if (userIds == null || userIds.isEmpty()) return;
        redisTemplate.opsForZSet().remove(key, userIds.toArray());
    }

    private static String keyOf(long couponId) {
        return KEY_FORMAT.formatted(couponId);
    }
}
