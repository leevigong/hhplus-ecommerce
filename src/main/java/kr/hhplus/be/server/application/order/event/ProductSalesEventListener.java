package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.domain.sales.ProductSalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import kr.hhplus.be.server.domain.order.OrderInfo;

/** 주문 확정 후 상품 판매량을 집계하는 리스너 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSalesEventListener {

    private final ProductSalesService productSalesService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderConfirmedEvent event) {
        log.info("ProductSalesEventListener 실행");

        try {
            productSalesService.add(event.getOrderInfo().orderItems());
            log.info("상품 판매량 기록 완료");

        } catch (Exception e) {
            log.info("상품 판매량 기록 실패");
        }
    }
}

