package kr.hhplus.be.server.domain.balance;

import java.util.List;

public interface UserBalanceHistoryRepository {

    UserBalanceHistory save(UserBalanceHistory userBalanceHistory);

    List<UserBalanceHistory> findByUserId(Long userId);
}
