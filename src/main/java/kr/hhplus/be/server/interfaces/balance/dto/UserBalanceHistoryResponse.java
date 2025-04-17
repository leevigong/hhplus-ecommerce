package kr.hhplus.be.server.interfaces.balance.dto;

import kr.hhplus.be.server.domain.balance.enums.TransactionType;
import kr.hhplus.be.server.domain.balance.UserBalanceHistoryInfo;
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

        public static UserBalanceHistoryV1 from(UserBalanceHistoryInfo userBalanceHistoryInfo) {
            return new UserBalanceHistoryV1(
                    userBalanceHistoryInfo.userId(),
                    userBalanceHistoryInfo.transactionType(),
                    userBalanceHistoryInfo.amount(),
                    userBalanceHistoryInfo.beforeBalance(),
                    userBalanceHistoryInfo.afterBalance(),
                    userBalanceHistoryInfo.createdAt()
            );
        }

    }
}
