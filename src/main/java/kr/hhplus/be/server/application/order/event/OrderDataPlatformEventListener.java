package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.application.order.port.OrderDataPlatformClient;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDataPlatformEventListener {

    private final OrderDataPlatformClient orderDataPlatformClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendToDataPlatform(OrderConfirmedEvent event) {
        log.info("OrderDataPlatformEventListener 실행");

        try {
            orderDataPlatformClient.sendOrderData(event.getOrderInfo());
            log.info("데이터 플랫폼에 주문 데이터 전송 완료");

        } catch (Exception e) {
            log.info("데이터 플랫폼에 주문 데이터 전송 실패");
        }
    }
}
