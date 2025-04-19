package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.domain.balance.enums.TransactionType;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBalanceServiceTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private UserBalanceHistoryRepository userBalanceHistoryRepository;

    @InjectMocks
    private UserBalanceService userBalanceService;

    private Long userId;
    private long initialBalance;
    private User user;
    private UserBalance userBalance;

    @BeforeEach
    void setUp() {
        userId = 1L;
        initialBalance = 100L;
        user = new User(userId, "testUser");
        userBalance = UserBalance.create(user, initialBalance);
    }

    @Test
    void 잔액_조회_성공() {
        //given
        when(userBalanceRepository.getByUserId(userId)).thenReturn(userBalance);

        // when
        UserBalanceInfo response = userBalanceService.getUserBalance(userId);

        // then
        assertThat(response.balance()).isEqualTo(userBalance.getBalance());
    }

    @Test
    void 잔액_충전_성공() {
        // given
        long chargeAmount = 500L;

        when(userBalanceRepository.getByUserId(userId)).thenReturn(userBalance);
        UserBalanceCommand.Charge command = new UserBalanceCommand.Charge(userId, chargeAmount);

        // when
        UserBalanceInfo response = userBalanceService.charge(command);

        // then
        long expectedBalance = initialBalance + chargeAmount;
        assertThat(userBalance.getBalance()).isEqualTo(expectedBalance);
        assertThat(response.balance()).isEqualTo(expectedBalance);

        verify(userBalanceHistoryRepository, times(1))
                .save(argThat(history ->
                        history.getUserId().equals(userId) &&
                                history.getAmount() == chargeAmount &&
                                history.getBeforeBalance() == initialBalance &&
                                history.getAfterBalance() == expectedBalance &&
                                history.getTransactionType() == TransactionType.CHARGE
                ));

        verify(userBalanceRepository, times(1)).save(userBalance);
    }

    @Test
    void 잔액_충전_실패_최소_충전_금액_만족하지_않음() {
        // given
        long chargeAmount = 50L;
        UserBalance userBalance = UserBalance.create(user, initialBalance);
        when(userBalanceRepository.getByUserId(userId)).thenReturn(userBalance);
        UserBalanceCommand.Charge command = new UserBalanceCommand.Charge(userId, chargeAmount);

        // when & then
        assertThatThrownBy(() -> userBalanceService.charge(command))
                .hasMessage(ApiErrorCode.INVALID_CHARGE_MIN_AMOUNT.getMessage());
    }

    @Test
    void 잔액_사용_성공() {
        // given
        long useAmount = 50L;
        UserBalanceCommand.Use command = new UserBalanceCommand.Use(userId, useAmount);

        when(userBalanceRepository.getByUserId(userId)).thenReturn(userBalance);

        // when
        UserBalanceInfo response = userBalanceService.use(command);

        // then
        long expectedBalance = initialBalance - useAmount; // 100 - 50 = 50
        assertThat(userBalance.getBalance()).isEqualTo(expectedBalance);
        assertThat(response.balance()).isEqualTo(expectedBalance);

        verify(userBalanceHistoryRepository, times(1))
                .save(argThat(history ->
                        history.getUserId().equals(userId) &&
                                history.getAmount() == useAmount &&
                                history.getBeforeBalance() == initialBalance &&
                                history.getAfterBalance() == expectedBalance &&
                                history.getTransactionType() == TransactionType.USE
                ));

        verify(userBalanceRepository, times(1)).save(userBalance);
    }


    @Test
    void 잔액_내역_저장_성공() {
        // given
        UserBalanceHistory history1 = UserBalanceHistory.of(userId, TransactionType.CHARGE, 50L, 100L, 150L);
        UserBalanceHistory history2 = UserBalanceHistory.of(userId, TransactionType.CHARGE, 30L, 150L, 180L);
        List<UserBalanceHistory> histories = Arrays.asList(history1, history2);

        when(userBalanceHistoryRepository.findAllByUserId(userId)).thenReturn(histories);

        // when
        List<UserBalanceHistoryInfo> responseList = userBalanceService.getUserBalanceHistory(userId);

        // then
        assertThat(responseList)
                .hasSize(histories.size())
                .extracting("amount", "beforeBalance", "afterBalance", "transactionType")
                .containsExactly(
                        tuple(history1.getAmount(), history1.getBeforeBalance(), history1.getAfterBalance(), history1.getTransactionType()),
                        tuple(history2.getAmount(), history2.getBeforeBalance(), history2.getAfterBalance(), history2.getTransactionType())
                );
    }
}
