package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.userCoupon.UserCouponRepository;
import kr.hhplus.be.server.support.contanier.TestContainerSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserCouponFacadeKafkaItTest extends TestContainerSupport {

    @Autowired
    UserCouponFacade userCouponFacade;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    UserCouponRepository userCouponRepository;

    Coupon coupon1;
    Coupon coupon2;
    Long userId1 = 123L;
    Long userId2 = 456L;

    @BeforeEach
    void setUp() {
        coupon1 = couponRepository.save(Coupon.createPercentage("TEST123", 10, 1, LocalDateTime.now().plusDays(1)));
        coupon2 = couponRepository.save(Coupon.createPercentage("TEST123", 10, 2, LocalDateTime.now().plusDays(1)));
        couponRepository.save(coupon1);
        couponRepository.save(coupon2);
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueCouponWithKafka_success() {
        // given
        var req = new UserCouponCriteria.PublishRequest(coupon1.getId(), userId1);

        // when
        userCouponFacade.issueCouponWithKafka(req);

        // then
        Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() ->
                        assertThat(userCouponRepository
                                .existsByCouponIdAndUserId(coupon1.getId(), userId1))
                                .isTrue());
    }

    @ExtendWith(OutputCaptureExtension.class)
    @Test
    @DisplayName("중복 발급시 이미 존재하는 쿠폰 에러 메세지 반환")
    void duplicateIssueLogsError(CapturedOutput output) {
        // given
        var req = new UserCouponCriteria.PublishRequest(coupon2.getId(), userId1);

        // when
        userCouponFacade.issueCouponWithKafka(req);
        userCouponFacade.issueCouponWithKafka(req);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(30))
                .untilAsserted(() ->
                        assertThat(output)
                                .contains("이미 존재하는 쿠폰"));
    }

    @ExtendWith(OutputCaptureExtension.class)
    @Test
    @DisplayName("매진된 쿠폰 발급 시 에러 로그 출력")
    void soldOutIssueLogsError(CapturedOutput output) {
        // given
        var req1 = new UserCouponCriteria.PublishRequest(coupon1.getId(), userId1);
        var req2 = new UserCouponCriteria.PublishRequest(coupon1.getId(), userId2); // 매진 이후 요청

        // when
        userCouponFacade.issueCouponWithKafka(req1);
        userCouponFacade.issueCouponWithKafka(req2);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() ->
                        assertThat(output)
                                .contains("더 이상 발급할 수 없습니다"));
    }
}
