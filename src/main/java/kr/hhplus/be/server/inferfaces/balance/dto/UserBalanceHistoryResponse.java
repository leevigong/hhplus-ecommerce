package kr.hhplus.be.server.inferfaces.balance.dto;

import kr.hhplus.be.server.domain.balance.TransactionType;
import kr.hhplus.be.server.domain.balance.UserBalanceHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class UserBalanceHistoryResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBalanceHistoryV1 {
        private Long userId;
        private TransactionType transactionType;
        private long amount;
        private long beforeBalance;
        private long afterBalance;
        private LocalDateTime createdAt;

        public static UserBalanceHistoryV1 from(UserBalanceHistory userBalanceHistory) {
            return new UserBalanceHistoryV1(userBalanceHistory.getUserId(),
                    userBalanceHistory.getTransactionType(),
                    userBalanceHistory.getAmount(),
                    userBalanceHistory.getBeforeBalance(),
                    userBalanceHistory.getAfterBalance(),
                    userBalanceHistory.getCreatedAt());
        }

    }
}
