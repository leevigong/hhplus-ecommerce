package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserCouponRepository userCouponRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserCouponRepository userCouponRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userCouponRepository = userCouponRepository;
    }

    public OrderInfo create(OrderCommand.Order orderCommand) {
        // 주문 항목 생성 및 상품 재고 체크/차감
        List<OrderItem> orderItems = new ArrayList<>();
        orderCommand.getOrderItems().forEach(op -> {
            Product product = productRepository.findById(op.getProductId());

            product.subStock(op.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(op.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItems.add(orderItem);
        });

        // 주문 생성
        Order order = Order.createOrder(orderCommand.getUserId(), orderItems);
        orderRepository.save(order);

        return OrderInfo.from(order);
    }

    public OrderInfo applyCoupon(OrderCommand.ApplyCoupon command) {
        Order order = orderRepository.findById(command.orderId());

        UserCoupon userCoupon = userCouponRepository.findByCouponId(command.userCouponId());

        order.applyCoupon(userCoupon);

        return OrderInfo.from(order);
    }

    public OrderInfo confirmOrder(OrderCommand.Confirm command) {
        Order order = orderRepository.findById(command.orderId());
        order.confirmOrder();

        return OrderInfo.from(order);
    }
}
