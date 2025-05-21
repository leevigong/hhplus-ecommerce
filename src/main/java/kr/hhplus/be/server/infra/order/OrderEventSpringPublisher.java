package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventSpringPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishOrderConfirmed(OrderInfo orderInfo) {
        eventPublisher.publishEvent(new OrderConfirmedEvent(orderInfo));
    }
}

