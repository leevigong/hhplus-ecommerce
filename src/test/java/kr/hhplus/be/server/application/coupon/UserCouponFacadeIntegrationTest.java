package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import kr.hhplus.be.server.domain.userCoupon.UserCouponRepository;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserCouponFacadeIntegrationTest {

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    private final Long userId = 1L;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = Coupon.createFixed("TEST123", 10, 100, LocalDateTime.now().plusDays(5));
        couponRepository.save(coupon);
    }

    @Test
    void 쿠폰_발급_성공() {
        // given
        UserCouponCriteria.Issue criteria = UserCouponCriteria.Issue.of(coupon.getId(), userId);

        // when
        userCouponFacade.issue(criteria);

        // then
        Coupon updated = couponRepository.getById(coupon.getId());
        assertThat(updated.getIssuedQuantity()).isEqualTo(1);

        List<UserCoupon> issued = userCouponRepository.findByUserId(userId);
        assertThat(issued).hasSize(1);
        assertThat(issued.get(0).getCoupon().getId())
                .isEqualTo(coupon.getId());
    }

    @Test
    void 만료된_쿠폰_발급_실패() {
        // given
        coupon.expire();
        couponRepository.save(coupon);
        UserCouponCriteria.Issue criteria = UserCouponCriteria.Issue.of(coupon.getId(), userId);

        // then
        assertThatThrownBy(() -> userCouponFacade.issue(criteria))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());
    }

    @Test
    void 매진된_쿠폰_발급_실패() {
        // given
        coupon.soldOut();
        couponRepository.save(coupon);
        UserCouponCriteria.Issue criteria = UserCouponCriteria.Issue.of(coupon.getId(), userId);

        // then
        assertThatThrownBy(() -> userCouponFacade.issue(criteria))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ApiErrorCode.COUPON_NOT_AVAILABLE_TO_ISSUE.getMessage());
    }

    @Test
    void 동일_사용자_쿠폰_중복_발급_실패() {
        // given
        UserCouponCriteria.Issue criteria = UserCouponCriteria.Issue.of(coupon.getId(), userId);

        // 1차 발급
        userCouponFacade.issue(criteria);

        // when & then
        assertThatThrownBy(() -> userCouponFacade.issue(criteria))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ApiErrorCode.ALREADY_USER_COUPON.getMessage());

        // DB 상태 확인
        Coupon updatedCoupon = couponRepository.getById(coupon.getId());
        assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(1);

        List<UserCoupon> issued = userCouponRepository.findByUserId(userId);
        assertThat(issued).hasSize(1);
    }
}
