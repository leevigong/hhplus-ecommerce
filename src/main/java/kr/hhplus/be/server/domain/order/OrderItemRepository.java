package kr.hhplus.be.server.domain.order;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    OrderItem findByOrderId(Long orderId);

}
