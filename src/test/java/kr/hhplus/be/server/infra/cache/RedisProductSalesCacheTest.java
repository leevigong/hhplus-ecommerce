package kr.hhplus.be.server.infra.cache;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.sales.ProductSalesInfo;
import kr.hhplus.be.server.infra.sales.RedisProductSalesCache;
import kr.hhplus.be.server.support.cache.CacheNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

class RedisProductSalesCacheTest {

    RedisTemplate<String, String> redisTemplate;
    ZSetOperations<String, String> zsetOps;
    RedisProductSalesCache cache;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        zsetOps = mock(ZSetOperations.class);
        when(redisTemplate.opsForZSet()).thenReturn(zsetOps);

        cache = new RedisProductSalesCache(redisTemplate);
    }

    @Test
    void 주문_아이템을_일별_ZSET에_수량만큼_누적한다() {
        // given
        OrderItem item = OrderItem.create(1L, 10, 1000);

        // when
        cache.add(List.of(item));

        // then
        String expectedKey = CacheNames.POPULAR_PRODUCT_SALES +
                             LocalDate.now().minusDays(2).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);

        verify(zsetOps).incrementScore(expectedKey, "product:1", 10);
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
