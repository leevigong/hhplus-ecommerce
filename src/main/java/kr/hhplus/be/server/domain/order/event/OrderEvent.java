package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.Getter;

public class OrderEvent {

    @Getter
    public static class Confirmed {

        private final OrderInfo orderInfo;

        public Confirmed(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
        }
    }
}
