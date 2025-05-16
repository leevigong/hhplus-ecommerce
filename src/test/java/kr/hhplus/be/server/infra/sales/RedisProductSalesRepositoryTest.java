package kr.hhplus.be.server.infra.sales;

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

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RedisProductSalesRepositoryTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ZSetOperations<String, String> zSetOps;

    RedisProductSalesRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RedisProductSalesRepository(redisTemplate);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
    }

    @Nested
    class GetTopSales {

        @Test
        void 데이터가_있으면_지정_갯수까지_반환한다() {
            int limit = 2;
            Set<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>();
            tuples.add(new DefaultTypedTuple<>("product:10", 15.0));
            tuples.add(new DefaultTypedTuple<>("product:20", 7.0));

            when(zSetOps.reverseRangeWithScores(anyString(), anyLong(), anyLong()))
                    .thenReturn(tuples);

            List<ProductSalesInfo.Popular> result =
                    repository.getTopSales(LocalDate.of(2025, 5, 16), limit);

            assertThat(result).hasSize(2)
                    .extracting(ProductSalesInfo.Popular::getProductId)
                    .containsExactly(10L, 20L);
            assertThat(result)
                    .extracting(ProductSalesInfo.Popular::getScore)
                    .containsExactly(15L, 7L);
        }

        @Test
        void 데이터가_없으면_빈_리스트를_반환한다() {
            when(zSetOps.reverseRangeWithScores(anyString(), anyLong(), anyLong()))
                    .thenReturn(Collections.emptySet());

            List<ProductSalesInfo.Popular> result =
                    repository.getTopSales(LocalDate.of(2025, 5, 16), 3);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetTopSalesRange {

        @Test
        void 캐시가_존재하면_ZUNIONSTORE없이_바로_반환한다() {
            LocalDate start = LocalDate.of(2025, 5, 14);
            LocalDate end = LocalDate.of(2025, 5, 16);
            int top = 3;
            String rangeKey = String.format("%s:%s-%s:top%d",
                    CacheNames.POPULAR_PRODUCT_SALES,
                    start.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE),
                    end.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE),
                    top);

            when(redisTemplate.hasKey(rangeKey)).thenReturn(true);

            Set<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>();
            tuples.add(new DefaultTypedTuple<>("product:3", 30.0));
            tuples.add(new DefaultTypedTuple<>("product:4", 28.0));

            when(zSetOps.reverseRangeWithScores(eq(rangeKey), anyLong(), anyLong()))
                    .thenReturn(tuples);

            List<ProductSalesInfo.Popular> result =
                    repository.getTopSalesRange(start, end, top);

            assertThat(result).hasSize(2)
                    .extracting(ProductSalesInfo.Popular::getProductId)
                    .containsExactly(3L, 4L);
        }

        @Test
        void 캐시_없지만_일일키가_있으면_합산후_반환한다() {
            LocalDate start = LocalDate.of(2025, 5, 15);
            LocalDate end = LocalDate.of(2025, 5, 16);
            int top = 2;
            String rangeKey = String.format("%s:%s-%s:top%d",
                    CacheNames.POPULAR_PRODUCT_SALES,
                    start.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE),
                    end.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE),
                    top);

            when(redisTemplate.hasKey(rangeKey)).thenReturn(false);
            when(redisTemplate.hasKey(argThat(k -> k.startsWith(CacheNames.POPULAR_PRODUCT_SALES))))
                    .thenReturn(true);

            org.mockito.Mockito.doReturn(1L)
                    .when(zSetOps).unionAndStore(anyString(), anyList(), anyString());
            org.mockito.Mockito.doNothing().when(redisTemplate).rename(anyString(), eq(rangeKey));
            org.mockito.Mockito.doReturn(true).when(redisTemplate).expire(eq(rangeKey), any());

            Set<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>();
            tuples.add(new DefaultTypedTuple<>("product:7", 12.0));
            when(zSetOps.reverseRangeWithScores(eq(rangeKey), anyLong(), anyLong()))
                    .thenReturn(tuples);

            List<ProductSalesInfo.Popular> result =
                    repository.getTopSalesRange(start, end, top);

            assertThat(result).hasSize(1)
                    .extracting(ProductSalesInfo.Popular::getProductId)
                    .containsExactly(7L);
        }

        @Test
        void 키가_하나도_없으면_빈_리스트를_반환한다() {
            when(redisTemplate.hasKey(anyString())).thenReturn(false);

            List<ProductSalesInfo.Popular> result =
                    repository.getTopSalesRange(LocalDate.of(2025, 5, 15),
                            LocalDate.of(2025, 5, 16), 5);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class Add {

        @Test
        void 호출마다_incrementScore된다() {
            var order1 = org.mockito.Mockito.mock(kr.hhplus.be.server.domain.order.OrderItem.class);
            when(order1.getProductId()).thenReturn(101L);
            when(order1.getQuantity()).thenReturn(3);

            var order2 = org.mockito.Mockito.mock(kr.hhplus.be.server.domain.order.OrderItem.class);
            when(order2.getProductId()).thenReturn(102L);
            when(order2.getQuantity()).thenReturn(1);

            when(zSetOps.incrementScore(anyString(), anyString(), anyDouble()))
                    .thenReturn(1.0);

            repository.add(List.of(order1, order2));

            org.mockito.Mockito.verify(zSetOps, org.mockito.Mockito.times(2))
                    .incrementScore(anyString(), anyString(), anyDouble());
        }
    }

    @Nested
    class GetAllSales {

        @Test
        void ZSET에_데이터가_있으면_전체를_리스트로_반환한다() {
            Set<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>();
            tuples.add(new DefaultTypedTuple<>("product:1", 10.0));
            tuples.add(new DefaultTypedTuple<>("product:2", 5.0));

            when(zSetOps.reverseRangeWithScores(anyString(), anyLong(), anyLong()))
                    .thenReturn(tuples);

            List<ProductSalesInfo.Popular> result = repository.getAllSales(LocalDate.of(2025, 5, 16));

            assertThat(result).hasSize(2)
                    .extracting(ProductSalesInfo.Popular::getProductId)
                    .containsExactly(1L, 2L);
        }

        @Test
        void ZSET이_비어_있으면_빈_리스트를_반환한다() {
            when(zSetOps.reverseRangeWithScores(anyString(), anyLong(), anyLong()))
                    .thenReturn(Collections.emptySet());

            List<ProductSalesInfo.Popular> result = repository.getAllSales(LocalDate.of(2025, 5, 16));

            assertThat(result).isEmpty();
        }
    }
}
