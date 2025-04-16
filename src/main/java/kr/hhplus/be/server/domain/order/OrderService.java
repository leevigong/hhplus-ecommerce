package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderInfo create(OrderCommand.Create createCommand) {
        List<OrderItem> orderItems = createCommand.toOrderItems();

        // 주문 생성
        Order order = Order.createOrder(createCommand.getUserId(), orderItems);

        // 총 결제 금액 계산
        order.calculateTotalPrice(orderItems);
        orderRepository.save(order);

        return OrderInfo.from(order);
    }

    public OrderInfo confirmOrder(OrderCommand.Confirm command) {
        Order order = orderRepository.getById(command.orderId());
        order.confirmOrder();

        return OrderInfo.from(order);
    }
}
