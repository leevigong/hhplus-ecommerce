package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final OrderRepository orderRepository;

    public UserCouponService(UserCouponRepository userCouponRepository,
                             OrderRepository orderRepository) {
        this.userCouponRepository = userCouponRepository;
        this.orderRepository = orderRepository;
    }

    public OrderInfo applyCoupon(OrderCommand.ApplyCoupon command) {
        Order order = orderRepository.getById(command.orderId());
        UserCoupon userCoupon = userCouponRepository.findById(command.userCouponId());
        order.applyCoupon(userCoupon);
        return OrderInfo.from(order);
    }
}
