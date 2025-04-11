package kr.hhplus.be.server.domain.balance;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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
        UserBalance userBalance = userBalanceRepository.findByUserId(userId);

        return UserBalanceInfo.from(userBalance);
    }

    public UserBalanceInfo charge(UserBalanceCommand.Charge command) {
        UserBalance userBalance = userBalanceRepository.findByUserId(command.getUserId());

        long beforePoint = userBalance.getBalance();
        UserBalance chargedUserBalance = userBalance.charge(command.getAmount());
        long afterPoint = chargedUserBalance.getBalance();

        UserBalanceHistory history = UserBalanceHistory.of(
                command.getUserId(),
                TransactionType.CHARGE,
                command.getAmount(),
                beforePoint,
                afterPoint
        );

        userBalanceHistoryRepository.save(history);
        userBalanceRepository.save(userBalance);

        return UserBalanceInfo.from(userBalance);
    }

    public UserBalanceInfo use(UserBalanceCommand.Charge command) {
        UserBalance userBalance = userBalanceRepository.findByUserId(command.getUserId());

        long beforePoint = userBalance.getBalance();
        UserBalance usedUserBalance = userBalance.use(command.getAmount());
        long afterPoint = usedUserBalance.getBalance();

        UserBalanceHistory history = UserBalanceHistory.of(
                command.getUserId(),
                TransactionType.USE,
                command.getAmount(),
                beforePoint,
                afterPoint
        );

        userBalanceHistoryRepository.save(history);
        userBalanceRepository.save(userBalance);

        return UserBalanceInfo.from(userBalance);
    }

    @Transactional(readOnly = true)
    public List<UserBalanceHistoryInfo> getUserBalanceHistory(Long userId) {
        List<UserBalanceHistory> histories = userBalanceHistoryRepository.findByUserId(userId);

        return histories.stream()
                .map(history -> UserBalanceHistoryInfo.from(history))
                .collect(Collectors.toList());
    }
}
