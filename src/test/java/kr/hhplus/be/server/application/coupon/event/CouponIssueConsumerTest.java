package kr.hhplus.be.server.application.coupon.event;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.userCoupon.UserCouponCommand;
import kr.hhplus.be.server.domain.userCoupon.UserCouponService;
import kr.hhplus.be.server.domain.userCoupon.event.CouponIssueRequestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CouponIssueConsumerTest {

    @Mock
    CouponService couponService;

    @Mock
    UserCouponService userCouponService;

    CouponIssueConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new CouponIssueConsumer(couponService, userCouponService);
    }

    @Test
    @DisplayName("쿠폰 발급 요청 이벤트를 처리한다")
    void listen_CouponIssueRequestEvent() {
        // given
        Long couponId = 1L;
        Long userId = 2L;

        Coupon coupon = Coupon.builder().id(couponId).build();
        CouponInfo.Issue issued = new CouponInfo.Issue(coupon);

        Mockito.when(couponService.issueCoupon(Mockito.any(CouponCommand.Issue.class)))
                .thenReturn(issued);

        // when
        org.springframework.kafka.support.Acknowledgment ack = Mockito.mock(org.springframework.kafka.support.Acknowledgment.class);
        consumer.listen(new CouponIssueRequestEvent(couponId, userId), ack);

        // then
        ArgumentCaptor<UserCouponCommand> captor = ArgumentCaptor.forClass(UserCouponCommand.class);
        Mockito.verify(userCouponService).createUserCoupon(captor.capture());

        UserCouponCommand cmd = captor.getValue();
        assertThat(cmd.coupon()).isEqualTo(coupon);
        assertThat(cmd.userId()).isEqualTo(userId);

        Mockito.verify(couponService)
                .issueCoupon(Mockito.any(CouponCommand.Issue.class));
        Mockito.verify(ack, Mockito.times(1)).acknowledge();
    }
}
