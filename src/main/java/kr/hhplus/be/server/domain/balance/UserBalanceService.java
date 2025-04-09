package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.inferfaces.balance.dto.UserBalanceHistoryResponse;
import kr.hhplus.be.server.inferfaces.balance.dto.UserBalanceResponse;
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
    public UserBalanceResponse.UserBalanceV1 getUserBalance(Long userId) {
        UserBalance userBalance = userBalanceRepository.findByUserId(userId);

        return UserBalanceResponse.UserBalanceV1.from(userBalance);
    }

    public UserBalanceResponse.UserBalanceV1 charge(UserBalanceCommand.Charge command) {
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

        return UserBalanceResponse.UserBalanceV1.from(userBalance);
    }

    @Transactional(readOnly = true)
    public List<UserBalanceHistoryResponse.UserBalanceHistoryV1> getUserBalanceHistory(Long userId) {
        List<UserBalanceHistory> histories = userBalanceHistoryRepository.findByUserId(userId);

        return histories.stream()
                .map(history -> UserBalanceHistoryResponse.UserBalanceHistoryV1.from(history))
                .collect(Collectors.toList());
    }
}
