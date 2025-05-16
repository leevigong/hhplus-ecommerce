package kr.hhplus.be.server.domain.sales;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSalesServiceTest {

    @Mock
    ProductSalesRepository productSalesRepository;

    @InjectMocks
    ProductSalesService productSalesService;

    @Test
    void 어제_집계_데이터가_존재하면_saveAll로_한번에_저장한다() {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ProductSalesInfo.Popular p1 = ProductSalesInfo.Popular.of(1L, 10L);
        ProductSalesInfo.Popular p2 = ProductSalesInfo.Popular.of(2L, 5L);
        when(productSalesRepository.getDailySales(yesterday))
                .thenReturn(List.of(p1, p2));

        // when
        productSalesService.saveYesterdaySales();

        // then
        ArgumentCaptor<List<ProductSales>> captor = ArgumentCaptor.forClass(List.class);
        verify(productSalesRepository, times(1)).saveAll(captor.capture());

        List<ProductSales> savedEntities = captor.getValue();
        assertThat(savedEntities).hasSize(2)
                .extracting(ProductSales::getProductId)
                .containsExactlyInAnyOrder(1L, 2L);

        // getScore() → ProductSales 엔티티 매핑 검증
        assertThat(savedEntities)
                .extracting(ProductSales::getCount)
                .containsExactlyInAnyOrder(10L, 5L);
    }

    @Test
    void 집계_데이터가_없으면_saveAll을_호출하지_않는다() {
        // given
        when(productSalesRepository.getDailySales(any(LocalDate.class)))
                .thenReturn(List.of());

        // when
        productSalesService.saveYesterdaySales();

        // then
        verify(productSalesRepository, never()).saveAll(any());
    }
}
