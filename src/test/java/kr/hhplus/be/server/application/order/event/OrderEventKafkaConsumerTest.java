package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.application.order.port.OrderDataPlatformClient;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.domain.sales.ProductSalesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventKafkaConsumerTest {

    @Mock
    OrderDataPlatformClient orderDataPlatformClient;

    @Mock
    ProductSalesService productSalesService;

    @InjectMocks
    OrderEventKafkaConsumer kafkaConsumer;

    @Mock
    OrderConfirmedEvent event;

    @Mock
    OrderInfo orderInfo;

    @Test
    @DisplayName("데이터 플랫폼에 주문 데이터 전송과 상품 판매량 기록을 성공한다")
    void listen_success() {
        // given
        List<OrderItem> items = List.of(mock(OrderItem.class));
        when(event.getOrderInfo()).thenReturn(orderInfo);
        when(orderInfo.orderItems()).thenReturn(items);
        Acknowledgment ack = mock(Acknowledgment.class);

        // when
        kafkaConsumer.listen(event, ack);

        // then
        verify(orderDataPlatformClient, times(1)).sendOrderData(orderInfo);
        verify(productSalesService, times(1)).add(items);
        verify(ack, times(1)).acknowledge();
        verifyNoMoreInteractions(orderDataPlatformClient, productSalesService);
    }

    @Test
    @DisplayName("상품 판매량 기록 예외 발생 시에도 OrderDataPlatformClient는 호출된다")
    void listen_whenProductSalesAddFails() {
        // given
        List<OrderItem> items = List.of(mock(OrderItem.class));
        when(event.getOrderInfo()).thenReturn(orderInfo);
        when(orderInfo.orderItems()).thenReturn(items);
        doThrow(new RuntimeException("DB error")).when(productSalesService).add(items);
        Acknowledgment ack = mock(Acknowledgment.class);

        // when & then
        assertThrows(RuntimeException.class, () -> kafkaConsumer.listen(event, ack));

        // verify
        verify(orderDataPlatformClient, times(1)).sendOrderData(orderInfo);
        verify(productSalesService, times(1)).add(items);
        verifyNoInteractions(ack); // 예외로 인해 커밋되지 않아야 함
    }
}
