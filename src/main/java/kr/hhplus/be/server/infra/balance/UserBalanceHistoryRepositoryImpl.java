package kr.hhplus.be.server.infra.balance;

import kr.hhplus.be.server.domain.balance.UserBalanceHistory;
import kr.hhplus.be.server.domain.balance.UserBalanceHistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBalanceHistoryRepositoryImpl implements UserBalanceHistoryRepository {

    @Override
    public List<UserBalanceHistory> findByUserId(Long userId) {
        return List.of();
    }

    @Override
    public UserBalanceHistory save(UserBalanceHistory userBalanceHistory) {
        return null;
    }
}
