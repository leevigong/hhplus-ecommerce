package kr.hhplus.be.server.domain.sales;

import kr.hhplus.be.server.support.contanier.ContainerTestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class ProductSalesServiceCacheTest extends ContainerTestSupport {

    @MockitoBean
    ProductSalesRepository productSalesRepository;

    @Autowired
    ProductSalesService productSalesService;

    @Test
    void 레포지토리는_1회만_실행되고_두_번째부터_캐시_HIT() {
        // given
        ProductSalesInfo.Popular ps1 = ProductSalesInfo.Popular.of(1L, 50L);
        ProductSalesInfo.Popular ps2 = ProductSalesInfo.Popular.of(2L, 30L);

        ProductSalesCommand.Popular cmd = ProductSalesCommand.Popular.of(
                4,
                LocalDate.of(2025, 5, 12),
                LocalDate.of(2025, 5, 15));

        when(productSalesRepository.getTopSalesRange(
                cmd.getStartDate(), cmd.getEndDate(), cmd.getTop()))
                .thenReturn(List.of(ps1, ps2));

        // when
        // 첫 호출 (Cache Miss)
        List<ProductSalesInfo.Popular> first = productSalesService.getProductSales(cmd);

        // 두 번째 호출 (Cache Hit)
        List<ProductSalesInfo.Popular> second = productSalesService.getProductSales(cmd);

        // then
        Mockito.verify(productSalesRepository, times(1))
                .getTopSalesRange(cmd.getStartDate(), cmd.getEndDate(), cmd.getTop());
        assertThat(second).isEqualTo(first);
    }

}
