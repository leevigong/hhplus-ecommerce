package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderEventSpringPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public OrderEventSpringPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(OrderConfirmedEvent event) {
        eventPublisher.publishEvent(event);
    }
}

