package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.application.order.port.OrderDataPlatformClient;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDataPlatformEventListenerTest {

    @Mock
    OrderDataPlatformClient dataPlatformClient;

    @InjectMocks
    OrderDataPlatformEventListener eventListener;

    OrderConfirmedEvent createEvent() {
        return mock(OrderConfirmedEvent.class);
    }

    @Nested
    class SendToDataPlatform {

        @Test
        @DisplayName("데이터 플랫폼 전송 성공 - 클라이언트 호출만 검증")
        void listen_success() {
            // when
            eventListener.sendToDataPlatform(createEvent());

            // then
            verify(dataPlatformClient).sendOrderData(any());
        }

        @Test
        @DisplayName("데이터 플랫폼 전송 실패 - 예외 로그 출력")
        void listen_failure() {
            // given
            doThrow(new RuntimeException("Network timeout"))
                    .when(dataPlatformClient).sendOrderData(any());

            // when & then
            assertDoesNotThrow(() -> eventListener.sendToDataPlatform(createEvent()));
            verify(dataPlatformClient).sendOrderData(any()); // 실행x println 안 찍힘
        }
    }
}
