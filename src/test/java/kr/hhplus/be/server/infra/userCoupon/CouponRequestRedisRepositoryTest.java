package kr.hhplus.be.server.infra.userCoupon;

import kr.hhplus.be.server.support.cache.CacheNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CouponRequestRedisRepositoryTest {

    private static final Duration TTL = Duration.ofMinutes(10);
    private static final long COUPON_ID = 1L;
    private static final long USER_ID = 99L;
    private static final String KEY = CacheNames.COUPON_CANDIDATES + ":" + COUPON_ID;

    @Mock
    RedisTemplate<String, Long> redisTemplate;
    @Mock
    ZSetOperations<String, Long> zSetOps;

    CouponRequestRedisRepository repository;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        repository = new CouponRequestRedisRepository(redisTemplate);
    }

    @Test
    void enqueue_새로운_키라면_TTL10분_설정() {
        when(zSetOps.addIfAbsent(eq(KEY), eq(USER_ID), anyDouble()))
                .thenReturn(Boolean.TRUE);

        boolean result = repository.enqueueCouponCandidate(COUPON_ID, USER_ID);

        assertThat(result).isTrue();
        verify(zSetOps).addIfAbsent(eq(KEY), eq(USER_ID), anyDouble());
        verify(redisTemplate).expire(KEY, TTL);
    }

    @Test
    void enqueue_기존_키라면_TTL을_재설정하지_않음() {
        when(zSetOps.addIfAbsent(eq(KEY), eq(USER_ID), anyDouble()))
                .thenReturn(Boolean.FALSE);

        boolean result = repository.enqueueCouponCandidate(COUPON_ID, USER_ID);

        assertThat(result).isFalse();
        verify(redisTemplate, never()).expire(anyString(), any());
    }


    @Test
    void fetch_정상적으로_range_반환() {
        when(zSetOps.range(KEY, 0, 4)).thenReturn(Set.of(1L, 2L));

        Set<Long> result = repository.fetchCouponCandidates(COUPON_ID, 5);

        assertThat(result).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void fetch_limit0이면_빈셋() {
        Set<Long> result = repository.fetchCouponCandidates(COUPON_ID, 0);

        assertThat(result).isEmpty();
        verifyNoInteractions(zSetOps);
    }

    @Test
    void remove_정상_호출() {
        repository.removeCouponCandidates(COUPON_ID, List.of(10L, 11L));

        verify(zSetOps).remove(eq(KEY), aryEq(new Object[]{10L, 11L}));
    }

    @Test
    void remove_빈컬렉션이면_호출안함() {
        repository.removeCouponCandidates(COUPON_ID, List.of());

        verifyNoInteractions(zSetOps);
    }
}
