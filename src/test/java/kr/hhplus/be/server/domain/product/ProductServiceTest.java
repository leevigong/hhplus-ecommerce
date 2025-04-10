package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.Category;
import kr.hhplus.be.server.inferfaces.product.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService getProductService;

    @Test
    void 상품_아이디로_상품_조회_성공() {
        // given
        Product product = new Product(1L, "상의", 10000, 10, Category.TOP);
        when(productRepository.findById(1L)).thenReturn(product);

        // when
        ProductInfo response = getProductService.getProductById(1L);

        // then
        assertEquals("상의", response.productName());
        assertEquals(10000, response.price());
    }
}
