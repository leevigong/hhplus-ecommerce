package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;
    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private Long userId;
    private Coupon coupon;
    private UserCoupon userCoupon;
    private Coupon expiredCoupon;
    private Coupon soldOutCoupon;

    @BeforeEach
    void setUp() {
        userId = 1L;

        coupon = Coupon.builder()
                .id(1L)
                .couponCode("TEST123")
                .discountType(DiscountType.FIXED)
                .discountAmount(10)
                .couponStatus(CouponStatus.ACTIVE)
                .maxIssuedQuantity(100)
                .issuedQuantity(0)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        expiredCoupon = Coupon.builder()
                .id(2L)
                .couponCode("TEST123")
                .discountType(DiscountType.FIXED)
                .discountAmount(10)
                .couponStatus(CouponStatus.ACTIVE)
                .maxIssuedQuantity(100)
                .issuedQuantity(0)
                .expiredAt(LocalDateTime.now().minusDays(1))
                .build();

        soldOutCoupon = Coupon.builder()
                .id(3L)
                .couponCode("TEST123")
                .discountType(DiscountType.FIXED)
                .discountAmount(10)
                .couponStatus(CouponStatus.ACTIVE)
                .maxIssuedQuantity(1)
                .issuedQuantity(1)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        userCoupon = userCoupon.builder()
                .id(1L)
                .coupon(coupon)
                .userId(userId)
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .build();

    }

    @Test
    void 유저_쿠폰_조회_성공() {
        // given
        when(userCouponRepository.findByUserId(userId)).thenReturn(List.of(userCoupon));

        // when
        List<UserCouponInfo> infos = couponService.getUserCoupons(userId);

        // then
        assertThat(infos).isNotNull().hasSize(1);
        UserCouponInfo info = infos.get(0);
        assertThat(info.couponId()).isEqualTo(coupon.getId());
        assertThat(info.couponCode()).isEqualTo(coupon.getCouponCode());
        assertThat(info.userCouponStatus()).isEqualTo(userCoupon.getUserCouponStatus());
        assertThat(info.discountAmount()).isEqualTo(coupon.getDiscountAmount());
    }

    @Test
    void 유저_쿠폰_발급_성공() {
        Long couponId = coupon.getId();
        Long userId = 100L;
        UserCouponCommand command = new UserCouponCommand(couponId, userId);

        when(couponRepository.findById(couponId)).thenReturn(coupon);
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(userCoupon);

        // when
        UserCouponInfo issuedInfo = couponService.issueCoupon(command);

        // then
        assertThat(issuedInfo).isNotNull();
        assertThat(issuedInfo.couponId()).isEqualTo(coupon.getId());
        assertThat(issuedInfo.couponCode()).isEqualTo(coupon.getCouponCode());
        assertThat(issuedInfo.discountAmount()).isEqualTo(coupon.getDiscountAmount());
    }

    @Test
    void 유저_쿠폰_발급시_기간만료되어_발급실패() {
        // given
        Long couponId = expiredCoupon.getId();
        UserCouponCommand command = new UserCouponCommand(couponId, userId);

        when(couponRepository.findById(couponId)).thenReturn(expiredCoupon);

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(command))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());
    }

    @Test
    void 유저_쿠폰_발급시_발급개수_초과되어_발급실패() {
        // given
        Long couponId = soldOutCoupon.getId();
        UserCouponCommand command = new UserCouponCommand(couponId, userId);

        when(couponRepository.findById(couponId)).thenReturn(soldOutCoupon);

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(command))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());
    }
}
