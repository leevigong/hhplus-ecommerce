package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.entity.BaseEntity;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    public static UserBalanceHistory of(Long userId, TransactionType transactionType, long amount, long beforeBalance, long afterBalance) {
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

}
