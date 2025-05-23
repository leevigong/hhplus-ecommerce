package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.OrderInfo;

public interface OrderEventPublisher {

    void publishOrderConfirmed(OrderInfo orderInfo);
}

