package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1L)
                .userId(1L)
                .totalPrice(100)
                .discountAmount(0)
                .finalPrice(100)
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    @Test
    void 쿠폰_적용_성공() {
        // given
        Long orderId = 1L;
        Long userCouponId = 1L;
        when(orderRepository.getById(orderId)).thenReturn(order);

        Coupon coupon = Coupon.builder()
                .couponCode("TEST")
                .discountType(DiscountType.FIXED)
                .discountAmount(10)
                .maxIssuedQuantity(100)
                .issuedQuantity(0)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .couponStatus(CouponStatus.ACTIVE)
                .build();

        UserCoupon userCoupon = UserCoupon.builder()
                .id(userCouponId)
                .coupon(coupon)
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .build();
        when(userCouponRepository.getById(userCouponId)).thenReturn(userCoupon);

        OrderCommand.ApplyCoupon applyCouponCommand = OrderCommand.ApplyCoupon.of(orderId, userCouponId);

        // when
        OrderInfo orderInfo = userCouponService.applyCoupon(applyCouponCommand);

        // then
        // 할인 금액 10 적용: 총액 100, 할인 후 최종 금액 90
        assertThat(orderInfo.orderId()).isEqualTo(orderId);
        assertThat(orderInfo.totalPrice()).isEqualTo(100);
        assertThat(orderInfo.discountAmount()).isEqualTo(10);
        assertThat(orderInfo.finalPrice()).isEqualTo(90);
    }

}
