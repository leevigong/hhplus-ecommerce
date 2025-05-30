package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.support.kafka.KafkaTopics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderEventKafkaPublisherTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    OrderEventKafkaPublisher publisher;

    @Test
    void 카프카_publish_성공() {
        // given
        OrderInfo orderInfo = new OrderInfo(1L, "PAID", 1000, 100, 900, LocalDateTime.now(), List.of(OrderItem.create(1L, 2, 1000)));
        OrderConfirmedEvent event = new OrderConfirmedEvent(orderInfo);

        publisher.publish(event);

        verify(kafkaTemplate, times(1))
                .send(KafkaTopics.ORDER_CONFIRMED, "1", event);
    }

}
