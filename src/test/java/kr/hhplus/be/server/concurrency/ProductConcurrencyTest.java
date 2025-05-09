package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.product.Category;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.support.concurrent.ConcurrentTestExecutor;
import kr.hhplus.be.server.support.concurrent.ConcurrentTestResult;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ProductConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product product;
    private ConcurrentTestExecutor executor;

    @BeforeEach
    void setUp() {
        product = productRepository.save(Product.create("맨투맨", 10000, 10, Category.TOP));
        executor = new ConcurrentTestExecutor();
    }

    @Test
    void 총_재고_10개일때_동시에_3명_재고_3개_차감_성공_잔여재고_1개() throws Throwable {
        // given
        OrderCommand.CreateOrderItem cmd1 = new OrderCommand.CreateOrderItem(product.getId(), 3, 10000);
        List<OrderCommand.CreateOrderItem> cmds = List.of(cmd1);
        List<Runnable> tasks = List.of(() -> productService.validateAndSubStockProducts(cmds));

        // when
        ConcurrentTestResult context = executor.execute(3, 3, tasks);

        // then
        System.out.println("성공 카운트: " + context.getSuccessCount().get());
        System.out.println("실패 카운트: " + context.getFailureCount().get());

        Product result = productRepository.getById(product.getId());
        System.out.println("남은 재고: " + result.getStockQuantity());
        assertThat(result.getStockQuantity()).isEqualTo(1);
    }

    @Test
    void 총_재고_10개일때_동시에_4명_재고_3개_차감_실패_재고부족발생() throws Throwable {
        // given
        OrderCommand.CreateOrderItem cmd1 = new OrderCommand.CreateOrderItem(product.getId(), 3, 10000);
        List<OrderCommand.CreateOrderItem> cmds = List.of(cmd1);
        List<Runnable> tasks = List.of(() -> productService.validateAndSubStockProducts(cmds));

        // when & then
        ConcurrentTestResult result = executor.execute(4, 4, tasks);
        assertThat(result.getErrors().get(0).getMessage()).isEqualTo(ApiErrorCode.INSUFFICIENT_STOCK.getMessage());

        Product updatedProduct = productRepository.getById(product.getId());
        System.out.println("남은 재고: " + updatedProduct.getStockQuantity());
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(1);
    }
}
