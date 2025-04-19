package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static kr.hhplus.be.server.domain.balance.UserBalance.CHARGE_MAX_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(statements = "DELETE FROM user_balance_history; DELETE FROM user_balance; DELETE FROM user;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserBalanceServiceItTest {

    @Autowired
    UserBalanceService userBalanceService;

    @Autowired
    UserBalanceRepository userBalanceRepository;

    @Autowired
    UserBalanceHistoryRepository userBalanceHistoryRepository;

    @Autowired
    UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.of("닉네임1"));
        userBalanceRepository.save(UserBalance.create(user, 1000L));
        userId = user.getId();
    }

    @Test
    void 잔액_조회_성공() {
        // when
        UserBalanceInfo info = userBalanceService.getUserBalance(userId);

        // then
        assertThat(info.userId()).isEqualTo(userId);
        assertThat(info.balance()).isEqualTo(1000L);
    }

    @Test
    void 잔액_충전_성공() {
        // given
        long chargeAmount = 100L; // 최소 충전 금액
        UserBalanceCommand.Charge command = new UserBalanceCommand.Charge(userId, chargeAmount);

        // when
        userBalanceService.charge(command);

        // then
        UserBalance userBalance = userBalanceRepository.getByUserId(userId);
        assertThat(userBalance.getBalance()).isEqualTo(1100); // 1000(초기) + 100(충전 금액)

        List<UserBalanceHistory> userBalanceHistories = userBalanceHistoryRepository.findAllByUserId(userId);
        assertThat(userBalanceHistories).hasSize(1);
        assertThat(userBalanceHistories.get(0).getBeforeBalance()).isEqualTo(1000L);
        assertThat(userBalanceHistories.get(0).getAmount()).isEqualTo(100);
        assertThat(userBalanceHistories.get(0).getAfterBalance()).isEqualTo(1100);
    }

    @ParameterizedTest
    @ValueSource(longs = {50L, 99L})
    void 잔액_충전_실패_최소_충전_금액_만족하지_않으면_예외_발생(long chargeAmount) {
        // given
        UserBalanceCommand.Charge command = new UserBalanceCommand.Charge(userId, chargeAmount);

        // when & then
        assertThatThrownBy(() -> userBalanceService.charge(command))
                .hasMessage(ApiErrorCode.INVALID_CHARGE_MIN_AMOUNT.getMessage());
    }

    @Test
    void 잔액_충전_실패_최대_충전_금액_초과시_예외_발생() {
        // given
        long chargeAmount = CHARGE_MAX_AMOUNT + 1;
        UserBalanceCommand.Charge command = new UserBalanceCommand.Charge(userId, chargeAmount);

        // when & then
        assertThatThrownBy(() -> userBalanceService.charge(command))
                .hasMessage(ApiErrorCode.INVALID_CHARGE_MAX_AMOUNT.getMessage());
    }

    @Test
    void 잔액_사용_성공() {
        // given
        long useAmount = 100L;
        UserBalanceCommand.Use command = new UserBalanceCommand.Use(userId, useAmount);

        // when
        userBalanceService.use(command);

        // then
        UserBalance userBalance = userBalanceRepository.getByUserId(userId);
        assertThat(userBalance.getBalance()).isEqualTo(900); // 1000(초기) - 100 (사용 금액)

        List<UserBalanceHistory> userBalanceHistories = userBalanceHistoryRepository.findAllByUserId(userId);
        assertThat(userBalanceHistories).hasSize(1);
        assertThat(userBalanceHistories.get(0).getBeforeBalance()).isEqualTo(1000L);
        assertThat(userBalanceHistories.get(0).getAmount()).isEqualTo(100);
        assertThat(userBalanceHistories.get(0).getAfterBalance()).isEqualTo(900);
    }

    @Test
    void 잔액_사용_실패_잔액_부족시_예외_발생() {
        // given
        long useAmount = 1001;
        UserBalanceCommand.Use command = new UserBalanceCommand.Use(userId, useAmount);

        // when & then
        assertThatThrownBy(() -> userBalanceService.use(command))
                .hasMessage(ApiErrorCode.INSUFFICIENT_BALANCE.getMessage());
    }
}
