package kr.hhplus.be.server.infra.cache;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.sales.ProductSalesInfo;
import kr.hhplus.be.server.infra.sales.RedisProductSalesRepository;
import kr.hhplus.be.server.support.cache.CacheNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

class RedisProductSalesRepositoryTest {

    RedisTemplate<String, String> redisTemplate;
    ZSetOperations<String, String> zsetOps;
    RedisProductSalesRepository cache;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        zsetOps = mock(ZSetOperations.class);
        when(redisTemplate.opsForZSet()).thenReturn(zsetOps);

        cache = new RedisProductSalesRepository(redisTemplate);
    }

    @Test
    void 주문_아이템을_일별_ZSET에_수량만큼_누적한다() {
        // given
        OrderItem item = OrderItem.create(1L, 10, 1000);

        // when
        cache.add(List.of(item));

        // then
        String expectedKey = CacheNames.POPULAR_PRODUCT_SALES + ":" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        verify(zsetOps).incrementScore(expectedKey, "product:1", 10);
        verify(redisTemplate).expire(eq(expectedKey), eq(java.time.Duration.ofDays(31)));
        verifyNoMoreInteractions(zsetOps); // 다른 호출 x
    }

    @Test
    void ZSET_결과를_DTO_리스트로_변환한다() {
        // given
        ZSetOperations.TypedTuple<String> t1 = typedTuple("product:1", 20d);
        ZSetOperations.TypedTuple<String> t2 = typedTuple("product:2", 15d);
        when(zsetOps.reverseRangeWithScores(anyString(), eq(0L), eq(2L)))
                .thenReturn(Set.of(t1, t2));

        // when
        List<ProductSalesInfo.Popular> result = cache.getTopSales(LocalDate.now(), 3);

        // then
        assertThat(result)
                .extracting(ProductSalesInfo.Popular::getProductId, ProductSalesInfo.Popular::getScore)
                .containsExactlyInAnyOrder(tuple(1L, 20L), tuple(2L, 15L));
    }

    @Test
    void 값이_없으면_빈_리스트_반환() {
        when(zsetOps.reverseRangeWithScores(anyString(), anyLong(), anyLong()))
                .thenReturn(Collections.emptySet());

        assertThat(cache.getTopSales(LocalDate.now(), 3)).isEmpty();
    }

    @Test
    void 기간합산_캐시없으면_ZUNIONSTORE후_TTL5분설정() {
        // given
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end   = LocalDate.now();
        int top = 3;

        String rangeKey = CacheNames.POPULAR_PRODUCT_SALES + ":"
                + start.format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                + end.format(DateTimeFormatter.BASIC_ISO_DATE) + ":top" + top;

        // rangeKey 는 존재하지 않음
        when(redisTemplate.hasKey(eq(rangeKey))).thenReturn(false);

        // start~end 각 일자 키는 존재한다고 가정
        start.datesUntil(end.plusDays(1))
             .forEach(d -> when(redisTemplate.hasKey(
                     eq(CacheNames.POPULAR_PRODUCT_SALES + ":" + d.format(DateTimeFormatter.BASIC_ISO_DATE))))
                     .thenReturn(true));

        when(zsetOps.reverseRangeWithScores(eq(rangeKey), eq(0L), eq((long) top - 1)))
                .thenReturn(Set.of(typedTuple("product:1", 5d)));

        // when
        cache.getTopSalesRange(start, end, top);

        // then
        verify(redisTemplate).expire(eq(rangeKey), eq(Duration.ofMinutes(5)));
    }

    /* 헬퍼: Mockito가 쉽게 만들 수 있도록 TypedTuple 구현체 제공 */
    private static ZSetOperations.TypedTuple<String> typedTuple(String member, double score) {
        return new ZSetOperations.TypedTuple<>() {
            @Override
            public String getValue() {
                return member;
            }

            @Override
            public Double getScore() {
                return score;
            }

            @Override
            public int compareTo(ZSetOperations.TypedTuple<String> o) {
                return 0;
            }
        };
    }
}
