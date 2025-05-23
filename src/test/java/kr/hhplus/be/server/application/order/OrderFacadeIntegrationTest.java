package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.port.OrderDataPlatformClient;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.sales.ProductSalesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class OrderFacadeIntegrationTest {

    @MockitoSpyBean
    private OrderEventPublisher orderEventPublisher;

    @Autowired
    private OrderFacade orderFacade;

    @MockitoSpyBean
    private OrderDataPlatformClient orderDataPlatformClient;

    @MockitoSpyBean
    private ProductSalesService productSalesService;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = productRepository.save(Product.builder()
                .name("테스트 상품")
                .price(10000)
                .stockQuantity(10)
                .build());
    }

    @Test
    void 주문_확정_후_OrderConfirmedEvent_발행_및_리스너_호출() {
        // given
        OrderCriteria.OrderItem orderItem = OrderCriteria.OrderItem.of(product.getId(), 2, product.getPrice());
        OrderCriteria.Create criteria = OrderCriteria.Create.of(1L, List.of(orderItem), null);

        // when
        orderFacade.order(criteria);

        // then
        // 이벤트 발행 검증
        verify(orderEventPublisher, times(1))
                .publishOrderConfirmed(any(OrderInfo.class));

        // 이벤트 리스너 검증
        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(orderDataPlatformClient, times(1)).sendOrderData(any(OrderInfo.class));
            verify(productSalesService, times(1)).add(any());
        });
    }
}
