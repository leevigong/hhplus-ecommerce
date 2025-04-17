package kr.hhplus.be.server.domain.balance;

public interface UserBalanceRepository {

    UserBalance save(UserBalance userBalance);

    UserBalance getByUserId(Long userId);

}
