package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserCouponRepository userCouponRepository;

    public OrderService(OrderRepository orderRepository,
                        UserCouponRepository userCouponRepository) {
        this.orderRepository = orderRepository;
        this.userCouponRepository = userCouponRepository;
    }

    public OrderInfo create(OrderCommand.Create createCommand) {
        List<OrderItem> items = createCommand.toOrderItems();
        UserCoupon coupon = null;
        if (createCommand.getUserCouponId() != null) {
            coupon = userCouponRepository.getById(createCommand.getUserCouponId());
        }

        Order order = (coupon != null)
                ? Order.createWithCoupon(createCommand.getUserId(), items, coupon)
                : Order.create(createCommand.getUserId(), items);

        orderRepository.save(order);
        return OrderInfo.from(order);
    }

    public OrderInfo confirmOrder(OrderCommand.Confirm command) {
        Order order = orderRepository.getById(command.orderId());
        order.confirmOrder();

        return OrderInfo.from(order);
    }
}
