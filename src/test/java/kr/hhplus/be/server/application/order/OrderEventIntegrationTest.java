package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.event.OrderEventKafkaConsumer;
import kr.hhplus.be.server.application.order.port.OrderDataPlatformClient;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.sales.ProductSalesService;
import kr.hhplus.be.server.infra.order.OrderEventKafkaPublisher;
import kr.hhplus.be.server.infra.order.OrderEventSpringPublisher;
import kr.hhplus.be.server.support.contanier.TestContainerSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
class OrderEventIntegrationTest extends TestContainerSupport {

    @MockitoSpyBean
    private OrderEventSpringPublisher publisher;

    @MockitoSpyBean
    private OrderEventKafkaPublisher kafkaPublisher;

    @MockitoSpyBean
    private OrderEventKafkaConsumer kafkaConsumer;

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
    @Disabled
    void 주문_확정_후_OrderConfirmedEvent_발행_및_리스너_호출() {
        // given
        OrderCriteria.OrderItem orderItem = OrderCriteria.OrderItem.of(product.getId(), 2, product.getPrice());
        OrderCriteria.Create criteria = OrderCriteria.Create.of(1L, List.of(orderItem), null);

        // when
        orderFacade.order(criteria);

        // then
        // 이벤트 발행 검증
        verify(publisher, times(1))
                .publish(any(OrderConfirmedEvent.class));

        // 이벤트 리스너 검증
        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(orderDataPlatformClient, times(1)).sendOrderData(any(OrderInfo.class));
            verify(productSalesService, times(1)).add(any());
        });
    }

    @Test
    void 주문_확정_후_OrderConfirmedEvent_카프카_처리() {
        // given
        OrderCriteria.OrderItem orderItem = OrderCriteria.OrderItem.of(product.getId(), 2, product.getPrice());
        OrderCriteria.Create criteria = OrderCriteria.Create.of(1L, List.of(orderItem), null);

        // when
        orderFacade.order(criteria);

        // then
        // 카프카 발행(퍼블리셔) 검증
        verify(kafkaPublisher, times(1))
                .publish(any(OrderConfirmedEvent.class));

        // 카프카 리스너(컨슈머) 검증
        await().atMost(5, SECONDS).untilAsserted(() ->
                verify(kafkaConsumer, times(1)).listen(any(OrderConfirmedEvent.class)));
    }
}
