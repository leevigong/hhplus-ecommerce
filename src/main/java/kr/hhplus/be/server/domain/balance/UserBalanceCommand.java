package kr.hhplus.be.server.domain.balance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserBalanceCommand {

    @Getter
    public static class Charge {

        private final Long userId;
        private final long amount;

        private Charge(Long userId, long amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Charge of(Long userId, long amount) {
            return new Charge(userId, amount);
        }
    }
}
