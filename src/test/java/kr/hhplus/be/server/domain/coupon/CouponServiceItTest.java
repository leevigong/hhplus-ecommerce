package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.support.exception.ApiErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CouponServiceItTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    void 쿠폰_발급_성공() {
        // given
        Coupon activeCoupon = Coupon.createFixed(
                "TEST123",
                10,
                100,
                LocalDateTime.now().plusDays(5)
        );
        couponRepository.save(activeCoupon);
        CouponCommand.Issue command = CouponCommand.Issue.of(activeCoupon.getId(), 1L);

        // when
        CouponInfo.Issue couponInfo = couponService.issueCoupon(command);

        // then
        Coupon coupon = couponRepository.getById(couponInfo.getCoupon().getId());
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
        CouponCommand.Issue command = CouponCommand.Issue.of(expiredCoupon.getId(), 1L);

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
        CouponCommand.Issue command = CouponCommand.Issue.of(soldOutCoupon.getId(), 1L);

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(command))
                .hasMessageContaining(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());
    }
}
