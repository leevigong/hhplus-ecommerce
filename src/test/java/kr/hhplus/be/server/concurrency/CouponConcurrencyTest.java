package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.concurrent.ConcurrentTestResult;
import kr.hhplus.be.server.support.concurrent.ConcurrentTestExecutor;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CouponConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    private ConcurrentTestExecutor executor;

    private User user;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.create("닉네임1"));
        coupon = couponRepository.save(Coupon.create("test123", DiscountType.PERCENTAGE, 10, 300, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1)));
        executor = new ConcurrentTestExecutor();
    }

    @Test
    void 선착순_쿠폰_발급_성공_동시성_300명_테스트() throws Throwable {
        // given
        CouponCommand command = CouponCommand.of(coupon.getId());

        List<Runnable> tasks = List.of(() -> couponService.issueCoupon(command));

        // when
        ConcurrentTestResult executed = executor.execute(300, 300, tasks);

        // then
        System.out.println("성공 카운트: " + executed.getSuccessCount().get());
        System.out.println("실패 카운트: " + executed.getFailureCount().get());

        Coupon result = couponRepository.getById(coupon.getId());
        assertThat(result.getIssuedQuantity()).isEqualTo(300);
        assertThat(result.getCouponStatus()).isEqualTo(CouponStatus.SOLD_OUT);
    }

    @Test
    void 선착순_쿠폰_발급_초과로_실패_동시성_301명_테스트() throws Throwable {
        // given
        CouponCommand command = CouponCommand.of(coupon.getId());

        List<Runnable> tasks = List.of(() -> couponService.issueCoupon(command));

        // when & then
        assertThatThrownBy(() -> executor.execute(301, 301, tasks))
                .hasMessage(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());
    }

    @Test
    void 선착순_쿠폰_발급_일부_성공_일부_실패() throws Throwable {
        // given: 이미 280개 발급된 쿠폰
        coupon.updateIssuedQuantity(280);
        couponRepository.save(coupon);
        CouponCommand command = CouponCommand.of(coupon.getId());
        List<Runnable> tasks = List.of(() -> couponService.issueCoupon(command));

        // when
        ConcurrentTestResult executed = executor.executeIgnoreErrors(300, 300, tasks);

        // then
        System.out.println("성공 카운트: " + executed.getSuccessCount().get());
        System.out.println("실패 카운트: " + executed.getFailureCount().get());

        // 20명만 성공, 나머지 280명은 실패
        assertThat(executed.getSuccessCount().get()).isEqualTo(20);
        assertThat(executed.getFailureCount().get()).isEqualTo(280);

        // 총 300개 발급, 상태는 SOLD_OUT
        Coupon result = couponRepository.getById(coupon.getId());
        assertThat(result.getIssuedQuantity()).isEqualTo(300);
        assertThat(result.getCouponStatus()).isEqualTo(CouponStatus.SOLD_OUT);
    }
}
