package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.entity.BaseEntity;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "user_balance_history")
public class UserBalanceHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private long amount;

    private long beforeBalance;

    private long afterBalance;

    public static UserBalanceHistory create(Long userId, TransactionType transactionType, long amount, long beforeBalance, long afterBalance) {
        if (amount < 0) {
            throw new ApiException(ApiErrorCode.NEGATIVE_BALANCE_HISTORY_NOT_ALLOWED);
        }

        return UserBalanceHistory.builder()
                .userId(userId)
                .transactionType(transactionType)
                .amount(amount)
                .beforeBalance(beforeBalance)
                .afterBalance(afterBalance)
                .build();
    }

    public static UserBalanceHistory createCharge(UserBalance beforeBalance, long amount) {
        if (amount < 0) {
            throw new ApiException(ApiErrorCode.NEGATIVE_BALANCE_HISTORY_NOT_ALLOWED);
        }

        return UserBalanceHistory.builder()
                .userId(beforeBalance.getUser().getId())
                .transactionType(TransactionType.CHARGE)
                .amount(amount)
                .beforeBalance(beforeBalance.getBalance())
                .afterBalance(beforeBalance.getBalance() + amount)
                .build();
    }

    public static UserBalanceHistory createUse(UserBalance beforeBalance, long amount) {
        if (amount < 0) {
            throw new ApiException(ApiErrorCode.NEGATIVE_BALANCE_HISTORY_NOT_ALLOWED);
        }

        return UserBalanceHistory.builder()
                .userId(beforeBalance.getUser().getId())
                .transactionType(TransactionType.USE)
                .amount(amount)
                .beforeBalance(beforeBalance.getBalance())
                .afterBalance(beforeBalance.getBalance() - amount) // NOTE: 생성자에서 계산
                .build();
    }
}
