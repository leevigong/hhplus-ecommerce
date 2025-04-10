package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private User user;
    private Coupon coupon;
    private UserCoupon userCoupon;
    private UserCoupon expiredUserCoupon;
    private Order order;

    @BeforeEach
    void setUp() {
        coupon = Coupon.builder()
                .couponCode("DISCOUNT1000")
                .discountType(DiscountType.FIXED)
                .discountAmount(1000)
                .maxIssuedQuantity(10)
                .issuedQuantity(0)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .couponStatus(CouponStatus.ACTIVE)
                .build();

        user = new User(1L, "홍길동");

        userCoupon = UserCoupon.builder()
                .coupon(coupon)
                .userId(user.getId())
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .build();

        expiredUserCoupon = UserCoupon.builder()
                .coupon(coupon)
                .userId(user.getId())
                .userCouponStatus(UserCouponStatus.EXPIRED)
                .build();

        order = Order.builder()
                .userId(user.getId())
                .totalPrice(5000)
                .discountAmount(0)
                .finalPrice(5000)
                .orderStatus(OrderStatus.PAID)
                .build();
    }

    @Test
    void 쿠폰을_적용하면_최종금액이_할인된다() {
        order.applyCoupon(userCoupon);

        assertThat(order.getDiscountAmount()).isEqualTo(1000);
        assertThat(order.getFinalPrice()).isEqualTo(4000);
        assertThat(order.getUserCoupon()).isNotNull();
        assertThat(order.getUserCoupon().getUserCouponStatus()).isEqualTo(UserCouponStatus.USED);
    }

    @Test
    void 만료된_쿠폰_적용시_예외() {
        assertThatThrownBy(() -> order.applyCoupon(expiredUserCoupon))
                .hasMessage(ApiErrorCode.INVALID_COUPON_STATUS.getMessage());
    }

    @Test
    void 주문_취소시_쿠폰도_복원된다() {
        order.applyCoupon(userCoupon);
        order.cancelOrder();

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(userCoupon.getUserCouponStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PENDING", "CANCELED"})
    void 결제_되지_않은_주문와_취소된_주문_취소시_예외(OrderStatus orderStatus) {
        order = Order.builder()
                .userId(user.getId())
                .totalPrice(5000)
                .discountAmount(0)
                .finalPrice(5000)
                .orderStatus(orderStatus)
                .build();

        assertThatThrownBy(order::cancelOrder)
                .hasMessage(ApiErrorCode.INVALID_ORDER_STATUS.getMessage());
    }
}
