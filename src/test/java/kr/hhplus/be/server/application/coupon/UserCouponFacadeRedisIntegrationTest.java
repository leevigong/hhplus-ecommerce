package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.userCoupon.UserCouponService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserCouponFacadeRedisIntegrationTest {

    @MockitoBean
    private UserCouponService userCouponService;

    @MockitoBean
    private CouponService couponService;

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    void requestPublishWithRedis_커맨드호출_검증() {
        // given
        long couponId = 77L;
        long userId = 33L;
        UserCouponCriteria.PublishRequest req = UserCouponCriteria.PublishRequest.of(couponId, userId);

        // when
        userCouponFacade.requestPublishWithRedis(req);

        // then
        ArgumentCaptor<CouponCommand.PublishRequest> captor =
                ArgumentCaptor.forClass(CouponCommand.PublishRequest.class);

        verify(userCouponService).requestPublishCoupon(captor.capture());

        CouponCommand.PublishRequest captured = captor.getValue();
        assertThat(captured.getCouponId()).isEqualTo(couponId);
        assertThat(captured.getUserId()).isEqualTo(userId);
    }


    @Test
    void publishCouponCandidate_발급가능쿠폰_목록만큼_호출() {
        // given
        Coupon coupon1 = Coupon.createFixed("TEST1", 10, 100, LocalDateTime.now().plusDays(5));
        Coupon coupon2 = Coupon.createFixed("TEST2", 50, 100, LocalDateTime.now().plusDays(5));
        couponRepository.save(coupon1);
        couponRepository.save(coupon2);

        CouponInfo.PublishableCoupon p1 = mock(CouponInfo.PublishableCoupon.class);
        when(p1.getCoupon()).thenReturn(coupon1);
        when(p1.getQuantity()).thenReturn(coupon1.getMaxIssuedQuantity() - coupon1.getIssuedQuantity());

        CouponInfo.PublishableCoupon p2 = mock(CouponInfo.PublishableCoupon.class);
        when(p2.getCoupon()).thenReturn(coupon2);
        when(p2.getQuantity()).thenReturn(coupon2.getMaxIssuedQuantity() - coupon2.getIssuedQuantity());

        when(couponService.getPublishableCoupons()).thenReturn(List.of(p1, p2));

        // when
        userCouponFacade.publishCouponCandidate();

        // then
        ArgumentCaptor<CouponCommand.Publish> captor = ArgumentCaptor.forClass(CouponCommand.Publish.class);

        verify(userCouponService, times(2)).publishCouponCandidate(captor.capture());

        List<CouponCommand.Publish> captured = captor.getAllValues();
        assertThat(captured)
                .extracting(CouponCommand.Publish::getCoupon)
                .containsExactlyInAnyOrder(coupon1, coupon2);
        assertThat(captured)
                .extracting(CouponCommand.Publish::getLimit)
                .containsExactlyInAnyOrder(
                        coupon1.getMaxIssuedQuantity() - coupon1.getIssuedQuantity(),
                        coupon2.getMaxIssuedQuantity() - coupon2.getIssuedQuantity()
                );
    }
}
