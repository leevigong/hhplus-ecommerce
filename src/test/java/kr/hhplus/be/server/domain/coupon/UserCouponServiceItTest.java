package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.order.OrderItem;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class UserCouponServiceItTest {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    private static final Long userId = 1L;

    private Coupon createCoupon(String couponCode, long discountAmount, int maxIssuedQuantity, CouponStatus status, LocalDateTime expiredAt) {
        return couponRepository.save(Coupon.create(couponCode, DiscountType.FIXED, discountAmount, maxIssuedQuantity, status, expiredAt));
    }

    private UserCoupon createUserCoupon(Coupon coupon, Long userId, UserCouponStatus status) {
        return userCouponRepository.save(UserCoupon.create(coupon, userId, status));
    }

    // Order 생성 및 저장 (userCoupon 없이)
    private Order createOrder(Long userId, long totalPrice, OrderStatus orderStatus) {
        List<OrderItem> items = List.of(OrderItem.create(1L, 1, totalPrice));
        Order order = Order.create(userId, items);
        return orderRepository.save(order);
    }

    // Order 생성 및 저장 (userCoupon 적용)
    private Order createOrderWithCoupon(Long userId, long totalPrice, UserCoupon userCoupon, OrderStatus orderStatus) {
        List<OrderItem> items = List.of(OrderItem.create(1L, 1, totalPrice));
        Order order = Order.createWithCoupon(userId, items, userCoupon);
        return orderRepository.save(order);
    }

    @Test
    void 쿠폰_적용_성공() {
        // given
        Coupon coupon = createCoupon("TEST123", 10, 100, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1));
        UserCoupon userCoupon = createUserCoupon(coupon, userId, UserCouponStatus.AVAILABLE);
        Order order = createOrder(userId, 100, OrderStatus.PENDING);

        OrderCommand.ApplyCoupon applyCouponCommand = OrderCommand.ApplyCoupon.of(order.getId(), userCoupon.getId());

        // when
        OrderInfo orderInfo = userCouponService.applyCoupon(applyCouponCommand);

        // then
        UserCoupon userCouponResult = userCouponRepository.getById(userCoupon.getId());
        assertThat(userCouponResult.getUserCouponStatus()).isEqualTo(UserCouponStatus.USED);

        Order orderResult = orderRepository.getById(order.getId());
        assertThat(orderResult.getOrderStatus()).isEqualTo(OrderStatus.PENDING); // 결제 전 상태
        assertThat(orderResult.getTotalPrice()).isEqualTo(100);
        assertThat(orderResult.getDiscountPrice()).isEqualTo(10);
        assertThat(orderResult.getFinalPrice()).isEqualTo(90);
    }

    @Test
    void 쿠폰_적용_실패_이미_적용된_쿠폰() {
        // given
        Coupon coupon1 = createCoupon("TEST123", 10, 100, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1));
        Coupon coupon2 = createCoupon("NEWTEST123", 10, 100, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1));
        UserCoupon userCoupon1 = createUserCoupon(coupon1, userId, UserCouponStatus.AVAILABLE);
        UserCoupon userCoupon2 = createUserCoupon(coupon2, userId, UserCouponStatus.AVAILABLE);
        Order order = createOrderWithCoupon(userId, 100, userCoupon1, OrderStatus.PENDING);

        OrderCommand.ApplyCoupon applyCouponCommand = OrderCommand.ApplyCoupon.of(order.getId(), userCoupon2.getId());

        // when & then
        assertThatThrownBy(() -> userCouponService.applyCoupon(applyCouponCommand))
                .hasMessage(ApiErrorCode.ALREADY_COUPON_APPLIED.getMessage());

        // 결제 실패로 order 업데이트 되지않음
        Order orderResult = orderRepository.getById(order.getId());
        assertThat(orderResult.getOrderStatus()).isEqualTo(order.getOrderStatus());
        assertThat(orderResult.getTotalPrice()).isEqualTo(order.getTotalPrice());
        assertThat(orderResult.getDiscountPrice()).isEqualTo(order.getDiscountPrice());
        assertThat(orderResult.getFinalPrice()).isEqualTo(order.getFinalPrice());
    }
}
