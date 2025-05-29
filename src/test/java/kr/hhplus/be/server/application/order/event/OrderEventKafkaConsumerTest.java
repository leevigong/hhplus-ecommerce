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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

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

        // when
        kafkaConsumer.listen(event);

        // then
        verify(orderDataPlatformClient, times(1)).sendOrderData(orderInfo);
        verify(productSalesService, times(1)).add(items);
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

        // when
        try {
            kafkaConsumer.listen(event);
        } catch (RuntimeException ignored) {

        }

        // then
        verify(orderDataPlatformClient, times(1)).sendOrderData(orderInfo);
        verify(productSalesService, times(1)).add(items);
    }
}
