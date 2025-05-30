package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import kr.hhplus.be.server.support.kafka.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventKafkaPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(OrderConfirmedEvent event) {
        kafkaTemplate.send(KafkaTopics.ORDER_CONFIRMED, event.getOrderInfo().orderId().toString(), event);
        log.info("Kafka 전송 - Message: {}", event.toString());
    }
}
