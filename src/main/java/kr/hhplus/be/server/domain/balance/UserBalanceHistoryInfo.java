package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.inferfaces.balance.dto.UserBalanceHistoryResponse;

import java.time.LocalDateTime;

public record UserBalanceHistoryInfo(
        Long userId,
        TransactionType transactionType,
        long amount,
        long beforeBalance,
        long afterBalance,
        LocalDateTime createdAt
){
    public static UserBalanceHistoryInfo from(UserBalanceHistory userBalanceHistory) {
        return new UserBalanceHistoryInfo(
                userBalanceHistory.getUserId(),
                userBalanceHistory.getTransactionType(),
                userBalanceHistory.getAmount(),
                userBalanceHistory.getBeforeBalance(),
                userBalanceHistory.getAfterBalance(),
                userBalanceHistory.getCreatedAt()
        );
    }
}
