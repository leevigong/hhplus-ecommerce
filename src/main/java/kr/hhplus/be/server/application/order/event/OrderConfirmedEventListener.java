package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.application.order.port.OrderDataPlatformClient;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.sales.ProductSalesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OrderConfirmedEventListener {

    private final OrderDataPlatformClient orderDataPlatformClient;
    private final ProductSalesService productSalesService;

    public OrderConfirmedEventListener(OrderDataPlatformClient orderDataPlatformClient, ProductSalesService productSalesService) {
        this.orderDataPlatformClient = orderDataPlatformClient;
        this.productSalesService = productSalesService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendToDataPlatform(OrderEvent.Confirmed event) {
        log.info("sendToDataPlatform 이벤트 실행");

        try {
            orderDataPlatformClient.sendOrderData(event.getOrderInfo());
            log.info("데이터 플랫폼에 주문 데이터 전송 완료");

        } catch (Exception e) {
            log.info("데이터 플랫폼에 주문 데이터 전송 실패");
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void storeProductSalesToRedis(OrderEvent.Confirmed event) {
        log.info("storeProductSalesToRedis 이벤트 실행");

        try {
            productSalesService.add(event.getOrderInfo().orderItems());
            log.info("상품 판매량 기록 완료");

        } catch (Exception e) {
            log.info("상품 판매량 기록 실패");
        }
    }
}
