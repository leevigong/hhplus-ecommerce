package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductSalesRankRepository productSalesRankRepository;
    @InjectMocks
    private ProductService productService;

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
        ProductInfo response = productService.getProductById(1L);

        // then
        assertEquals("상의", response.productName());
        assertEquals(10000, response.price());
    }

}
