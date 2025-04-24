package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.DiscountType;
import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.userCoupon.UserCouponStatus;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.List;

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
        // 쿠폰 생성
        coupon = Coupon.builder()
                .couponCode("DISCOUNT1000")
                .discountType(DiscountType.FIXED)
                .discountAmount(1000)
                .maxIssuedQuantity(10)
                .issuedQuantity(0)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .couponStatus(CouponStatus.ACTIVE)
                .build();

        // 사용자 생성
        user = User.builder()
                .id(1L)
                .nickname("홍길동")
                .build();

        // 사용 가능한 유저쿠폰
        userCoupon = UserCoupon.builder()
                .coupon(coupon)
                .userId(user.getId())
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .build();

        // 만료된 유저쿠폰
        expiredUserCoupon = UserCoupon.builder()
                .coupon(coupon)
                .userId(user.getId())
                .userCouponStatus(UserCouponStatus.EXPIRED)
                .build();

        // 주문 생성 및 결제 완료 상태로 세팅
        List<OrderItem> items = List.of(OrderItem.create(1L, 5000, 1));
        order = Order.create(user.getId(), items);
        order.confirmOrder();
    }

    @Test
    void 쿠폰을_적용하면_최종금액이_할인된다() {
        order.applyCoupon(userCoupon);

        assertThat(order.getDiscountPrice()).isEqualTo(1000);
        assertThat(order.getFinalPrice()).isEqualTo(4000);
        assertThat(order.getUserCoupon()).isNotNull();
        assertThat(order.getUserCoupon().getUserCouponStatus())
                .isEqualTo(UserCouponStatus.USED);
    }

    @Test
    void 만료된_쿠폰_적용시_예외() {
        assertThatThrownBy(() -> order.applyCoupon(expiredUserCoupon))
                .hasMessage(ApiErrorCode.USER_COUPON_EXPIRED.getMessage());
    }

    @Test
    void 주문_취소시_쿠폰도_복원된다() {
        order.applyCoupon(userCoupon);
        order.cancelOrder();

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(userCoupon.getUserCouponStatus())
                .isEqualTo(UserCouponStatus.AVAILABLE);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"CANCELED"})
    void 결제_되지_않은_주문와_취소된_주문_취소시_예외(OrderStatus status) {
        // 새로운 주문 생성
        List<OrderItem> items = List.of(OrderItem.create(1L, 5000, 1));
        order = Order.create(user.getId(), items);

        // 상태 강제 변경
        if (status == OrderStatus.CANCELED) {
            order.cancelOrder();
        }

        assertThatThrownBy(order::cancelOrder)
                .hasMessage(ApiErrorCode.INVALID_ORDER_STATUS.getMessage());
    }
}
