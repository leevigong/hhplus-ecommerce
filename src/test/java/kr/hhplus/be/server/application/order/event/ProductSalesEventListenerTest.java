package kr.hhplus.be.server.application.order.event;

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

@ExtendWith(MockitoExtension.class)
class ProductSalesEventListenerTest {

    @Mock
    ProductSalesService productSalesService;

    @Mock
    OrderConfirmedEvent event;

    @Mock
    OrderInfo orderInfo;

    @InjectMocks
    ProductSalesEventListener listener;

    @Test
    @DisplayName("상품 판매량 기록 성공")
    void listen_success() {
        // given
        List<OrderItem> items = List.of(mock(OrderItem.class));
        when(event.getOrderInfo()).thenReturn(orderInfo);
        when(orderInfo.orderItems()).thenReturn(items);

        // when
        listener.handle(event);

        // then
        verify(productSalesService).add(items);
    }

    @Test
    @DisplayName("상품 판매량 기록 실패 - 예외 로그 출력")
    void listen_failure() {
        // given
        List<OrderItem> items = List.of(mock(OrderItem.class));
        when(event.getOrderInfo()).thenReturn(orderInfo);
        when(orderInfo.orderItems()).thenReturn(items);
        doThrow(new RuntimeException("DB error"))
                .when(productSalesService).add(items);

        // when & then
        listener.handle(event);
        verify(productSalesService).add(items);
    }
}
