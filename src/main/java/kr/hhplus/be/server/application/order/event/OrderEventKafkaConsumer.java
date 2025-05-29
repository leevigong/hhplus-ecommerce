package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.application.order.port.OrderDataPlatformClient;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.domain.sales.ProductSalesService;
import kr.hhplus.be.server.support.kafka.KafkaGroups;
import kr.hhplus.be.server.support.kafka.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventKafkaConsumer {

    private final OrderDataPlatformClient orderDataPlatformClient;
    private final ProductSalesService productSalesService;

    public OrderEventKafkaConsumer(OrderDataPlatformClient orderDataPlatformClient, ProductSalesService productSalesService) {
        this.orderDataPlatformClient = orderDataPlatformClient;
        this.productSalesService = productSalesService;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CONFIRMED, groupId = KafkaGroups.ORDER_CONSUMER)
    public void listen(OrderConfirmedEvent event, Acknowledgment ack) {
        log.info("ORDER_CONFIRMED 수신: {}", event.getOrderInfo().orderId());

        try {
            orderDataPlatformClient.sendOrderData(event.getOrderInfo());
            log.info("데이터 플랫폼에 주문 데이터 전송 완료");

            productSalesService.add(event.getOrderInfo().orderItems());
            log.info("상품 판매량 기록 완료");

            ack.acknowledge();
        } catch (Exception ex) {
            log.error("ORDER_CONFIRMED 처리 실패, 메시지 재시도 예정", ex);
            throw ex;
        }
    }
}
