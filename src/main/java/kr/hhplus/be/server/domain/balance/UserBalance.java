package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.entity.BaseEntity;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_balance")
public class UserBalance extends BaseEntity {

    public final static long USE_MIN_AMOUNT = 1;
    public final static long CHARGE_MIN_AMOUNT = 100;
    public final static long CHARGE_MAX_AMOUNT = 1_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_balance_user"))
    private User user;

    private long balance;

    @Builder
    private UserBalance(Long id, User user, long balance) {
        this.id = id;
        this.user = user;
        this.balance = balance;
    }

    public static UserBalance create(User user, long balance) {
        if (balance < 0) {
            throw new ApiException(ApiErrorCode.NEGATIVE_BALANCE_NOT_ALLOWED);
        }

        return UserBalance.builder()
                .user(user)
                .balance(balance)
                .build();
    }

    public UserBalance charge(long amount) {
        if (amount < CHARGE_MIN_AMOUNT) {
            throw new ApiException(ApiErrorCode.INVALID_CHARGE_MIN_AMOUNT);
        }
        if (amount > CHARGE_MAX_AMOUNT) {
            throw new ApiException(ApiErrorCode.INVALID_CHARGE_MAX_AMOUNT);
        }
        this.balance += amount;
        return this;
    }

    public UserBalance use(long amount) {
        if (amount < USE_MIN_AMOUNT) {
            throw new ApiException(ApiErrorCode.INVALID_USE_MIN_AMOUNT);
        }
        if (amount > balance) {
            throw new ApiException(ApiErrorCode.INSUFFICIENT_BALANCE);
        }
        this.balance -= amount;
        return this;
    }
}
