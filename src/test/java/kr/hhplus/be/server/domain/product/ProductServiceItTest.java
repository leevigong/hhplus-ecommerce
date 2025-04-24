package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class ProductServiceItTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductSalesRankRepository productSalesRankRepository;

    private Long productId;

    @BeforeEach
    void setUp() {
        Product product1 = Product.create("상품1", 1000, 10, Category.TOP);
        Product product2 = Product.create("상품1", 1000, 10, Category.TOP);
        Product product3 = Product.create("상품1", 1000, 10, Category.TOP);
        productRepository.save(product1);
        productId = product1.getId();
    }

    @Test
    void 재고_검증_성공() {
        // given
        OrderCommand.CreateOrderItem createOrderItem = new OrderCommand.CreateOrderItem(productId, 5, 1000L);
        List<OrderCommand.CreateOrderItem> createOrderItems = List.of(createOrderItem);

        // when
        productService.validateAndSubStockProducts(createOrderItems);

        // then
        Product product = productRepository.getById(productId);
        assertThat(product.getStockQuantity()).isEqualTo(5);
    }

    @Test
    void 재고_검증_실패_재고_부족시_예외_발생() {
        // given
        OrderCommand.CreateOrderItem createOrderItem = new OrderCommand.CreateOrderItem(productId, 11, 1000L);
        List<OrderCommand.CreateOrderItem> createOrderItems = List.of(createOrderItem);

        // when & then
        assertThatThrownBy(() -> productService.validateAndSubStockProducts(createOrderItems))
                .hasMessage(ApiErrorCode.INSUFFICIENT_STOCK.getMessage());

        Product product = productRepository.getById(productId);
        assertThat(product.getStockQuantity()).isEqualTo(10);
    }
}
