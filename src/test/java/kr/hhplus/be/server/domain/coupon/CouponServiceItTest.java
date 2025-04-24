package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.userCoupon.UserCouponRepository;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class CouponServiceItTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    private final Long userId = 1L;

    @Test
    void 쿠폰_발급_성공() {
        // given
        Coupon activeCoupon = Coupon.create(
                "TEST123",
                DiscountType.FIXED,
                10,
                100,
                CouponStatus.ACTIVE,
                LocalDateTime.now().plusDays(5)
        );
        couponRepository.save(activeCoupon);
        CouponCommand command = new CouponCommand(activeCoupon.getId());

        // when
        CouponInfo couponInfo = couponService.issueCoupon(command);

        // then
        Coupon coupon = couponRepository.getById(couponInfo.coupon().getId());
        assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
    }

    @Test
    void 기간_만료된_쿠폰_발급_실패() {
        // given
        Coupon expiredCoupon = Coupon.create(
                "EXPIRED123",
                DiscountType.FIXED,
                10,
                100,
                CouponStatus.EXPIRED,
                LocalDateTime.now().minusDays(1)
        );
        couponRepository.save(expiredCoupon);
        CouponCommand command = new CouponCommand(expiredCoupon.getId());

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(command))
                .hasMessageContaining(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());
    }

    @Test
    void 매진된_쿠폰_발급_실패() {
        // given
        Coupon soldOutCoupon = Coupon.builder()
                .couponCode("SOLDOUT123")
                .discountType(DiscountType.FIXED)
                .discountAmount(10)
                .couponStatus(CouponStatus.ACTIVE)
                .maxIssuedQuantity(1)
                .issuedQuantity(1)   // 이미 최대 발급 개수 도달
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();
        couponRepository.save(soldOutCoupon);
        CouponCommand command = new CouponCommand(soldOutCoupon.getId());

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(command))
                .hasMessageContaining(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());
    }
}
