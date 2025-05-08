package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.support.cache.CacheNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Objects;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EnableCaching
class ProductServiceCacheTest {

    @Autowired
    ProductService productService;

    @MockitoSpyBean
    ProductSalesRankRepository productSalesRankRepository;

    @Autowired
    RedisCacheManager redisCacheManager;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(redisCacheManager.getCache(CacheNames.POPULAR_PRODUCTS)).clear();
    }

    @Test
    void 캐시적용하면_레포지토리_호출은_1번만_된다() {
        // given
        String scope = RankingScope.THREE_DAYS.name();

        // when
        productService.getProductSalesRank(scope); // 캐시 x, DB 0
        productService.getProductSalesRank(scope); // 캐시 0, DB x
        productService.getProductSalesRank(scope); // 캐시 0, DB x
        productService.getProductSalesRank(scope); // 캐시 0, DB x

        // then
        verify(productSalesRankRepository, times(1))
                .findByRankingScope(RankingScope.THREE_DAYS);
    }
}
