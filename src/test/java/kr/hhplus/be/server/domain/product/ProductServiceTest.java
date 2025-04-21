package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.product.enums.Category;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService getProductService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.create("상의", 10000, 10, Category.TOP);
    }

    @Test
    void 상품_아이디로_상품_조회_성공() {
        // given
        when(productRepository.getById(1L)).thenReturn(product);

        // when
        ProductInfo response = getProductService.getProductById(1L);

        // then
        assertEquals("상의", response.productName());
        assertEquals(10000, response.price());
    }

    @Test
    void 재고_검증_성공() {
        // given
        OrderCommand.CreateOrderItem createOrderItem = new OrderCommand.CreateOrderItem(100L, 5, 2000L);
        List<OrderCommand.CreateOrderItem> createOrderItems = List.of(createOrderItem);
        when(productRepository.getById(100L)).thenReturn(product);

        // when & then
        assertThatCode(() -> getProductService.validateAndSubStockProducts(createOrderItems))
                .doesNotThrowAnyException();
    }

    @Test
    void 재고_검증_실패() {
        // given
        OrderCommand.CreateOrderItem createOrderItem = new OrderCommand.CreateOrderItem(100L, 15, 2000L);
        List<OrderCommand.CreateOrderItem> createOrderItems = List.of(createOrderItem);
        when(productRepository.getById(100L)).thenReturn(product);

        // when & then
        assertThatThrownBy(() -> getProductService.validateAndSubStockProducts(createOrderItems))
                .hasMessage(ApiErrorCode.INSUFFICIENT_STOCK.getMessage());
    }
}
