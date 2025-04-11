package kr.hhplus.be.server.domain.balance;

public record UserBalanceInfo (
        Long userId,
        long balance
){
    public static UserBalanceInfo from(UserBalance userBalance) {
        return new UserBalanceInfo(userBalance.getUser().getId(), userBalance.getBalance());
    }
}
