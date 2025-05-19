package kr.hhplus.be.server.infra.userCoupon;

import kr.hhplus.be.server.domain.userCoupon.UserCouponCachePort;
import kr.hhplus.be.server.support.cache.CacheNames;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;

@Repository
public class UserCouponRedisCache implements UserCouponCachePort {

    private static final Duration TTL = Duration.ofMinutes(10);
    private static final String KEY_PREFIX = CacheNames.COUPON_CANDIDATES + ":";

    private final RedisTemplate<String, Long> redisTemplate;

    public UserCouponRedisCache(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean enqueueCouponCandidate(long couponId, long userId) {
        String key = KEY_PREFIX + couponId;
        double score = Instant.now().getEpochSecond();

        Boolean added = redisTemplate.opsForZSet()
                .addIfAbsent(key, userId, score);

        if (Boolean.TRUE.equals(added)) {
            redisTemplate.expire(key, TTL);
        }
        return Boolean.TRUE.equals(added);
    }

    public Set<Long> fetchCouponCandidates(long couponId, int limit) {
        if (limit <= 0) {
            return Set.of();
        }

        String key = KEY_PREFIX + couponId;
        return redisTemplate.opsForZSet().range(key, 0, limit - 1);
    }

    public void removeCouponCandidates(long couponId, Collection<? extends Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        String key = KEY_PREFIX + couponId;
        redisTemplate.opsForZSet().remove(key, userIds.toArray());
    }
}
