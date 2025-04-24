package kr.hhplus.be.server.domain.userCoupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.DiscountType;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserCouponTest {

    private Coupon validCoupon;
    private Coupon expiredCoupon;
    private User user;

    @BeforeEach
    void setUp() {
        validCoupon = Coupon.builder()
                .couponCode("TEST100")
                .discountType(DiscountType.FIXED)
                .discountAmount(1000)
                .maxIssuedQuantity(10)
                .issuedQuantity(1)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .couponStatus(CouponStatus.ACTIVE)
                .build();

        expiredCoupon = Coupon.builder()
                .couponCode("EXPIRED123")
                .discountType(DiscountType.FIXED)
                .discountAmount(1000)
                .maxIssuedQuantity(1)
                .issuedQuantity(1)
                .expiredAt(LocalDateTime.now().minusDays(1))
                .couponStatus(CouponStatus.ACTIVE)
                .build();

        user = User.create("이다은");
    }

    @Test
    void 쿠폰_사용_성공() {
        UserCoupon userCoupon = UserCoupon.builder()
                .coupon(validCoupon)
                .userId(user.getId())
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .build();

        userCoupon.use();

        assertThat(userCoupon.getUserCouponStatus()).isEqualTo(UserCouponStatus.USED);
    }

    @Test
    void 만료된_쿠폰_사용시_예외() {
        UserCoupon userCoupon = UserCoupon.builder()
                .coupon(expiredCoupon)
                .userId(user.getId())
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .build();

        assertThatThrownBy(userCoupon::use)
                .isInstanceOf(ApiException.class)
                .hasMessage(ApiErrorCode.USER_COUPON_EXPIRED.getMessage());
    }

    @Test
    void 사용한_쿠폰_취소_성공() {
        UserCoupon userCoupon = UserCoupon.builder()
                .coupon(validCoupon)
                .userId(user.getId())
                .userCouponStatus(UserCouponStatus.USED)
                .build();

        userCoupon.cancel();

        assertThat(userCoupon.getUserCouponStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
    }

    @Test
    void 사용하지_않은_쿠폰_취소시_예외() {
        UserCoupon userCoupon = UserCoupon.builder()
                .coupon(validCoupon)
                .userId(user.getId())
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .build();

        assertThatThrownBy(userCoupon::cancel)
                .hasMessage(ApiErrorCode.INVALID_COUPON_STATUS.getMessage());
    }

    @Test
    void 만료된_쿠폰_취소시_예외() {
        UserCoupon userCoupon = UserCoupon.builder()
                .coupon(expiredCoupon)
                .userId(user.getId())
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .build();

        assertThatThrownBy(userCoupon::cancel)
                .hasMessage(ApiErrorCode.USER_COUPON_EXPIRED.getMessage());
    }
}
