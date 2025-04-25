package kr.hhplus.be.server.domain.balance;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableRetry
public class UserBalanceService {

    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceHistoryRepository userBalanceHistoryRepository;

    public UserBalanceService(UserBalanceRepository userBalanceRepository,
                              UserBalanceHistoryRepository userBalanceHistoryRepository) {
        this.userBalanceRepository = userBalanceRepository;
        this.userBalanceHistoryRepository = userBalanceHistoryRepository;
    }

    @Transactional(readOnly = true)
    public UserBalanceInfo getUserBalance(Long userId) {
        UserBalance userBalance = userBalanceRepository.getByUserId(userId);

        return UserBalanceInfo.from(userBalance);
    }

    @Retryable(
            value = ObjectOptimisticLockingFailureException.class, // 재시도 대상
            maxAttempts = 2,                          // 최대 재시도 횟수 (기본값 3)
            backoff = @Backoff(delay = 500)        // 재시도 간격 0.5초 (기본값 1초)
    )
    @Transactional
    public UserBalanceInfo charge(UserBalanceCommand.Charge command) {
        UserBalance userBalance = userBalanceRepository.getByUserId(command.getUserId());

        long beforePoint = userBalance.getBalance();
        UserBalance chargedUserBalance = userBalance.charge(command.getAmount());
        long afterPoint = chargedUserBalance.getBalance();
        userBalanceRepository.save(chargedUserBalance);

        UserBalanceHistory history = UserBalanceHistory.create(
                command.getUserId(),
                TransactionType.CHARGE,
                command.getAmount(),
                beforePoint,
                afterPoint
        );
        userBalanceHistoryRepository.save(history);

        return UserBalanceInfo.from(chargedUserBalance);
    }

    @Transactional
    public UserBalanceInfo use(UserBalanceCommand.Use command) {
        UserBalance userBalance = userBalanceRepository.getByUserId(command.getUserId());

        long beforePoint = userBalance.getBalance();
        UserBalance usedUserBalance = userBalance.use(command.getAmount());
        long afterPoint = usedUserBalance.getBalance();
        userBalanceRepository.save(userBalance);

        UserBalanceHistory history = UserBalanceHistory.create(
                command.getUserId(),
                TransactionType.USE,
                command.getAmount(),
                beforePoint,
                afterPoint
        );
        userBalanceHistoryRepository.save(history);

        return UserBalanceInfo.from(userBalance);
    }

    @Transactional(readOnly = true)
    public List<UserBalanceHistoryInfo> getUserBalanceHistory(Long userId) {
        List<UserBalanceHistory> histories = userBalanceHistoryRepository.findAllByUserId(userId);

        return histories.stream()
                .map(history -> UserBalanceHistoryInfo.from(history))
                .collect(Collectors.toList());
    }
}
