package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.Getter;

/** 주문 확정 이후 발생하는 이벤트 */
@Getter
public class OrderConfirmedEvent {

    private final OrderInfo orderInfo;

    public OrderConfirmedEvent(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }
}
