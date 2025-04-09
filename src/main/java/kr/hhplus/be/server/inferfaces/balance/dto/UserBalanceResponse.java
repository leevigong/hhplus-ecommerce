package kr.hhplus.be.server.inferfaces.balance.dto;


import kr.hhplus.be.server.domain.balance.UserBalance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserBalanceResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBalanceV1 {

        private Long userId;
        private long balance;

        public static UserBalanceV1 from(UserBalance userBalance) {
            return new UserBalanceV1(userBalance.getUser().getId(), userBalance.getBalance());
        }

    }
}
