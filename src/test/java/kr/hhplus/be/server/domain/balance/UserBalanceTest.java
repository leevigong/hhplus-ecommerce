package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserBalanceTest {

    private User user;
    private UserBalance userBalance;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testUser");
    }

    @Test
    void 잔액이_음수면_예외가_발생한다() {
        assertThatThrownBy(() -> UserBalance.create(user, -1))
                .isInstanceOf(ApiException.class)
                .hasMessage(ApiErrorCode.NEGATIVE_BALANCE_NOT_ALLOWED.getMessage());
    }

    @Nested
    class charge {

        @BeforeEach
        void setUp() {
            userBalance = UserBalance.create(user, 0);
        }

        @ParameterizedTest
        @ValueSource(longs = {100L, 500L, 1_000_000L})
        void 충전_성공(long amount) {
            UserBalance chargedUserPoint = userBalance.charge(amount);

            assertThat(chargedUserPoint.getBalance()).isEqualTo(amount);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, 99L, -10L})
        void 충전_실패_100원_미만(long amount) {
            assertThatThrownBy(() -> userBalance.charge(amount))
                    .isInstanceOf(ApiException.class)
                    .hasMessage(ApiErrorCode.INVALID_CHARGE_MIN_AMOUNT.getMessage());
        }

        @Test
        void 충전_실패_최대_초과시_예외_발생() {
            long amount = 1_000_001L;

            assertThatThrownBy(() -> userBalance.charge(amount))
                    .isInstanceOf(ApiException.class)
                    .hasMessage(ApiErrorCode.INVALID_CHARGE_MAX_AMOUNT.getMessage());
        }
    }

    @Nested
    class use {

        @BeforeEach
        void setUp() {
            userBalance = UserBalance.create(user, 100);
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 50L, 100L})
        void 사용_성공(long amount) {
            UserBalance usedUserBalance = userBalance.use(amount);

            assertThat(usedUserBalance.getBalance()).isEqualTo(100 - amount);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L})
        void 사용_실패_최소값_미만이면_예외_발생(long amount) {
            assertThatThrownBy(() -> userBalance.use(amount))
                    .isInstanceOf(ApiException.class)
                    .hasMessage(ApiErrorCode.INVALID_USE_MIN_AMOUNT.getMessage());
        }

        @ParameterizedTest
        @ValueSource(longs = {101L, 2000L})
        void 사용_실패_잔액_부족시_예외_발생(long amount) {
            assertThatThrownBy(() -> userBalance.use(amount))
                    .isInstanceOf(ApiException.class)
                    .hasMessage(ApiErrorCode.INSUFFICIENT_BALANCE.getMessage());
        }
    }
}
