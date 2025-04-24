package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.application.order.port.OrderDataPlatformClient;
import kr.hhplus.be.server.domain.order.OrderInfo;
import org.springframework.stereotype.Component;

@Component
public class FakeOrderDataPlatformClient implements OrderDataPlatformClient {

    @Override
    public void sendOrderData(OrderInfo orderInfo) {
        System.out.println("주문 데이터 전송: " + orderInfo);
    }
}
