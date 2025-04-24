package kr.hhplus.be.server.application.order.port;

import kr.hhplus.be.server.domain.order.OrderInfo;

public interface OrderDataPlatformClient {

    void sendOrderData(OrderInfo orderInfo);
}
