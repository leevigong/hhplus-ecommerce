package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {

    OrderItem findByOrderId(Long orderId);
}
