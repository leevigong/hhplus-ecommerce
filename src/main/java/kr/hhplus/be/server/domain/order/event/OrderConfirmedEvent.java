package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 주문 확정 이후 발생하는 이벤트 */
@Getter
@NoArgsConstructor
public class OrderConfirmedEvent {

    private OrderInfo orderInfo;

    public OrderConfirmedEvent(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    @Override
    public String toString() {
        return "{" +
               "orderInfo=" + orderInfo +
               '}';
    }
}
