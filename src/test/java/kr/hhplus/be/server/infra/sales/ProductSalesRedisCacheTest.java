package kr.hhplus.be.server.infra.sales;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.sales.ProductSalesInfo;
import kr.hhplus.be.server.support.cache.CacheNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductSalesRedisCacheTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ZSetOperations<String, String> zSetOps;

    ProductSalesRedisCache cache;

    @BeforeEach
    void setUp() {
        cache = new ProductSalesRedisCache(redisTemplate);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
    }

    @Test
    void 주문_아이템을_일별_ZSET에_수량만큼_누적하고_TTL31일_설정한다() {
        // given
        OrderItem item = OrderItem.create(1L, 10, 1000);

        // when
        cache.add(List.of(item));

        // then
        String key = CacheNames.POPULAR_PRODUCT_SALES + ":" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        verify(zSetOps).incrementScore(key, "product:1", 10);
        verify(redisTemplate).expire(eq(key), eq(Duration.ofDays(31)));
        verifyNoMoreInteractions(zSetOps);
    }

    @Nested
    class Add {
        @Test
        void incrementScore_가_아이템수만큼_호출된다() {
            // given
            var o1 = mock(OrderItem.class);
            var o2 = mock(OrderItem.class);
            when(o1.getProductId()).thenReturn(101L);
            when(o1.getQuantity()).thenReturn(3);
            when(o2.getProductId()).thenReturn(102L);
            when(o2.getQuantity()).thenReturn(1);
            when(zSetOps.incrementScore(anyString(), anyString(), anyDouble())).thenReturn(1.0);

            // when
            cache.add(List.of(o1, o2));

            // then
            verify(zSetOps, times(2)).incrementScore(anyString(), anyString(), anyDouble());
        }
    }

    @Nested
    class GetTopSales {
        @Test
        void 데이터가_있으면_DTO_리스트로_변환된다() {
            // given
            var t1 = typedTuple("product:1", 20d);
            var t2 = typedTuple("product:2", 15d);
            when(zSetOps.reverseRangeWithScores(anyString(), eq(0L), eq(2L))).thenReturn(Set.of(t1, t2));

            // when
            List<ProductSalesInfo.Popular> result = cache.getTopSales(LocalDate.now(), 3);

            // then
            assertThat(result).extracting(ProductSalesInfo.Popular::getProductId, ProductSalesInfo.Popular::getScore)
                    .containsExactlyInAnyOrder(tuple(1L, 20L), tuple(2L, 15L));
        }

        @Test
        void 값이_없으면_빈_리스트를_반환한다() {
            // given
            when(zSetOps.reverseRangeWithScores(anyString(), anyLong(), anyLong())).thenReturn(Collections.emptySet());
            // when
            assertThat(cache.getTopSales(LocalDate.now(), 3)).isEmpty();
            // then
        }

        @Test
        void limit_개수만큼_반환한다() {
            // given
            int limit = 2;
            var tuples = new LinkedHashSet<ZSetOperations.TypedTuple<String>>();
            tuples.add(new DefaultTypedTuple<>("product:10", 15.0));
            tuples.add(new DefaultTypedTuple<>("product:20", 7.0));
            when(zSetOps.reverseRangeWithScores(anyString(), anyLong(), anyLong())).thenReturn(tuples);

            // when
            List<ProductSalesInfo.Popular> result = cache.getTopSales(LocalDate.of(2025, 5, 16), limit);

            // then
            assertThat(result).hasSize(2)
                    .extracting(ProductSalesInfo.Popular::getProductId)
                    .containsExactly(10L, 20L);
        }
    }

    @Nested
    class GetTopSalesRange {
        @Test
        void 캐시가_없으면_ZUNIONSTORE수행후_TTL5분을_설정한다() {
            // given
            LocalDate start = LocalDate.now().minusDays(3);
            LocalDate end = LocalDate.now();
            int top = 3;
            String rangeKey = String.format("%s:%s-%s:top%d", CacheNames.POPULAR_PRODUCT_SALES,
                    start.format(DateTimeFormatter.BASIC_ISO_DATE), end.format(DateTimeFormatter.BASIC_ISO_DATE), top);

            when(redisTemplate.hasKey(rangeKey)).thenReturn(false);
            start.datesUntil(end.plusDays(1)).forEach(d ->
                    when(redisTemplate.hasKey(CacheNames.POPULAR_PRODUCT_SALES + ":" + d.format(DateTimeFormatter.BASIC_ISO_DATE))).thenReturn(true));
            when(zSetOps.reverseRangeWithScores(eq(rangeKey), eq(0L), eq((long) top - 1)))
                    .thenReturn(Set.of(typedTuple("product:1", 5d)));

            // when
            cache.getTopSalesRange(start, end, top);

            // then
            verify(redisTemplate).expire(eq(rangeKey), eq(Duration.ofMinutes(5)));
        }

        @Test
        void 캐시가_존재하면_ZUNIONSTORE없이_데이터를_반환한다() {
            // given
            LocalDate start = LocalDate.of(2025, 5, 14);
            LocalDate end = LocalDate.of(2025, 5, 16);
            int top = 3;
            String rangeKey = String.format("%s:%s-%s:top%d", CacheNames.POPULAR_PRODUCT_SALES,
                    start.format(DateTimeFormatter.BASIC_ISO_DATE), end.format(DateTimeFormatter.BASIC_ISO_DATE), top);

            when(redisTemplate.hasKey(rangeKey)).thenReturn(true);
            var tuples = new LinkedHashSet<ZSetOperations.TypedTuple<String>>();
            tuples.add(new DefaultTypedTuple<>("product:3", 30.0));
            tuples.add(new DefaultTypedTuple<>("product:4", 28.0));
            when(zSetOps.reverseRangeWithScores(eq(rangeKey), anyLong(), anyLong())).thenReturn(tuples);

            // when
            List<ProductSalesInfo.Popular> result = cache.getTopSalesRange(start, end, top);

            // then
            assertThat(result).extracting(ProductSalesInfo.Popular::getProductId).containsExactly(3L, 4L);
        }

        @Test
        void 일일키만_있으면_합산후_데이터를_반환한다() {
            // given
            LocalDate start = LocalDate.of(2025, 5, 15);
            LocalDate end = LocalDate.of(2025, 5, 16);
            int top = 2;
            String rangeKey = String.format("%s:%s-%s:top%d", CacheNames.POPULAR_PRODUCT_SALES,
                    start.format(DateTimeFormatter.BASIC_ISO_DATE), end.format(DateTimeFormatter.BASIC_ISO_DATE), top);

            when(redisTemplate.hasKey(rangeKey)).thenReturn(false);
            when(redisTemplate.hasKey(argThat(k -> k.startsWith(CacheNames.POPULAR_PRODUCT_SALES)))).thenReturn(true);
            when(zSetOps.unionAndStore(anyString(), anyList(), anyString())).thenReturn(1L);
            doNothing().when(redisTemplate).rename(anyString(), eq(rangeKey));
            when(redisTemplate.expire(eq(rangeKey), any())).thenReturn(true);
            when(zSetOps.reverseRangeWithScores(eq(rangeKey), anyLong(), anyLong()))
                    .thenReturn(Set.of(new DefaultTypedTuple<>("product:7", 12.0)));

            // when
            List<ProductSalesInfo.Popular> result = cache.getTopSalesRange(start, end, top);

            // then
            assertThat(result).extracting(ProductSalesInfo.Popular::getProductId).containsExactly(7L);
        }

        @Test
        void 키가_없으면_빈_리스트를_반환한다() {
            // given
            when(redisTemplate.hasKey(anyString())).thenReturn(false);

            // when
            List<ProductSalesInfo.Popular> result = cache.getTopSalesRange(LocalDate.of(2025, 5, 15), LocalDate.of(2025, 5, 16), 5);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetAllSales {
        @Test
        void ZSET에_데이터가_있으면_전체를_반환한다() {
            // given
            var tuples = new LinkedHashSet<ZSetOperations.TypedTuple<String>>();
            tuples.add(new DefaultTypedTuple<>("product:1", 10.0));
            tuples.add(new DefaultTypedTuple<>("product:2", 5.0));
            when(zSetOps.reverseRangeWithScores(anyString(), anyLong(), anyLong())).thenReturn(tuples);

            // when
            List<ProductSalesInfo.Popular> result = cache.getAllSales(LocalDate.of(2025, 5, 16));

            // then
            assertThat(result).extracting(ProductSalesInfo.Popular::getProductId).containsExactly(1L, 2L);
        }

        @Test
        void ZSET이_비어있으면_빈_리스트를_반환한다() {
            // given
            when(zSetOps.reverseRangeWithScores(anyString(), anyLong(), anyLong())).thenReturn(Collections.emptySet());

            // when
            List<ProductSalesInfo.Popular> result = cache.getAllSales(LocalDate.of(2025, 5, 16));

            // then
            assertThat(result).isEmpty();
        }
    }

    private static ZSetOperations.TypedTuple<String> typedTuple(String member, double score) {
        return new ZSetOperations.TypedTuple<>() {
            @Override public String getValue() { return member; }
            @Override public Double getScore() { return score; }
            @Override public int compareTo(ZSetOperations.TypedTuple<String> o) { return 0; }
        };
    }
}
