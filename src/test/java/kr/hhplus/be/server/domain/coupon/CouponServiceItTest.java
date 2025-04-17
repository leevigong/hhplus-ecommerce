package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

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
    void 유저_쿠폰_발급_성공() {
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
        UserCouponCommand command = new UserCouponCommand(activeCoupon.getId(), userId);

        // when
        UserCouponInfo userCouponInfo = couponService.issueCoupon(command);

        // then
        UserCoupon userCoupon = userCouponRepository.getById(userCouponInfo.userCouponId());
        assertThat(userCoupon.getCoupon().getId()).isEqualTo(activeCoupon.getId());
        assertThat(userCoupon.getUserCouponStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
    }

    @Test
    void 유저_쿠폰_발급시_쿠폰_기간_만료되어_발급_실패() {
        // given
        Coupon expiredCoupon = Coupon.create(
                "EXPIRED123",
                DiscountType.FIXED,
                10,
                100,
                CouponStatus.ACTIVE, // 서비스 로직에서 유효기간 체크
                LocalDateTime.now().minusDays(1)
        );
        couponRepository.save(expiredCoupon);
        UserCouponCommand command = new UserCouponCommand(expiredCoupon.getId(), userId);

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(command))
                .hasMessageContaining(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());

        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        assertThat(userCoupons.size()).isEqualTo(0);
    }

    @Test
    void 유저_쿠폰_발급시_발급_개수가_초과되어_발급_실패() {
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
        UserCouponCommand command = new UserCouponCommand(soldOutCoupon.getId(), userId);

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(command))
                .hasMessageContaining(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());

        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        assertThat(userCoupons.size()).isEqualTo(0);
    }
}
