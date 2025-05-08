package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.concurrent.ConcurrentTestExecutor;
import kr.hhplus.be.server.support.concurrent.ConcurrentTestResult;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserCouponFacadeLockTest {

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    private ConcurrentTestExecutor executor;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = couponRepository.save(Coupon.createPercentage("test123", 10, 300, LocalDateTime.now().plusDays(1)));
        executor = new ConcurrentTestExecutor();
    }

    @Test
    void 선착순_쿠폰_발급_성공_동시성_300명_테스트() throws Throwable {
        // given
        List<Runnable> tasks = LongStream.rangeClosed(1, 300)
                .mapToObj(userId -> (Runnable) () -> {
                    UserCouponCriteria criteria = UserCouponCriteria.of(coupon.getId(), userId);
                    userCouponFacade.issue(criteria);
                })
                .toList();

        // when
        ConcurrentTestResult result = executor.execute(300, tasks);

        // then
        System.out.println("성공 카운트: " + result.getSuccessCount().get());
        System.out.println("실패 카운트: " + result.getFailureCount().get());

        Coupon updatedCoupon = couponRepository.getById(coupon.getId());
        assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(300);
        assertThat(updatedCoupon.getCouponStatus()).isEqualTo(CouponStatus.SOLD_OUT);
    }

    @Test
    void 선착순_쿠폰_발급_일부_성공_일부_실패() throws Throwable {
        // given: 이미 280개 발급된 쿠폰
        coupon.updateIssuedQuantity(280);
        couponRepository.save(coupon);

        List<Runnable> tasks = LongStream.rangeClosed(1, 300)
                .mapToObj(userId -> (Runnable) () -> {
                    UserCouponCriteria criteria = UserCouponCriteria.of(coupon.getId(), userId);
                    userCouponFacade.issue(criteria);
                })
                .toList();

        // when
        ConcurrentTestResult result = executor.execute(300, tasks);

        // then
        System.out.println("성공 카운트: " + result.getSuccessCount().get());
        System.out.println("실패 카운트: " + result.getFailureCount().get());

        // 20명만 성공, 나머지 280명은 실패
        assertThat(result.getSuccessCount().get()).isEqualTo(20);
        assertThat(result.getFailureCount().get()).isEqualTo(280);
        assertThat(result.getErrors())
                .hasSize(280)
                .first()
                .isInstanceOf(ApiException.class)
                .extracting(Throwable::getMessage)
                .isEqualTo(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());


        // 총 300개 발급, 상태는 SOLD_OUT
        Coupon updatedCoupon = couponRepository.getById(coupon.getId());
        assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(300);
        assertThat(updatedCoupon.getCouponStatus()).isEqualTo(CouponStatus.SOLD_OUT);
    }
}
