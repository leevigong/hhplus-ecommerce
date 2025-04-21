package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = Coupon.builder()
                .couponCode("WELCOME100")
                .discountType(DiscountType.FIXED)
                .discountAmount(1000)
                .maxIssuedQuantity(5)
                .issuedQuantity(0)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .couponStatus(CouponStatus.ACTIVE)
                .build();
    }

    @Test
    void 발급_가능한_쿠폰_체크() {
        assertThat(coupon.isAvailableToIssue()).isTrue();
    }

    @Test
    void 쿠폰_발급시_수량증가() {
        coupon.issue();

        assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
    }

    @Test
    void 발급_불가능시_예외발생() {
        coupon.expire();

        assertThatThrownBy(() -> coupon.issue())
                .hasMessage(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());
    }

    @Test
    void 최대_발급시_상태변경() {
        for (int i = 0; i < 5; i++) {
            coupon.issue();
        }

        assertThat(coupon.getCouponStatus()).isEqualTo(CouponStatus.SOLD_OUT);
        assertThat(coupon.isAvailableToIssue()).isFalse();
    }

    @Test
    void 쿠폰_만료_처리() {
        coupon.expire();

        assertThat(coupon.getCouponStatus()).isEqualTo(CouponStatus.EXPIRED);
    }
}
