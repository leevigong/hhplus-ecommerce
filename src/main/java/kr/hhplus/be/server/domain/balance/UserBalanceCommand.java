package kr.hhplus.be.server.domain.balance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserBalanceCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Charge {

        private Long userId;
        private long amount;

        public static Charge of(Long userId, long amount) {
            return new Charge(userId, amount);
        }
    }
}
