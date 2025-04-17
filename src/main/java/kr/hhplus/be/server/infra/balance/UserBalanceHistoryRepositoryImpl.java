package kr.hhplus.be.server.infra.balance;

import kr.hhplus.be.server.domain.balance.UserBalanceHistory;
import kr.hhplus.be.server.domain.balance.UserBalanceHistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBalanceHistoryRepositoryImpl implements UserBalanceHistoryRepository {

    private final UserBalanceHistoryJpaRepository userBalanceHistoryJpaRepository;

    public UserBalanceHistoryRepositoryImpl(UserBalanceHistoryJpaRepository userBalanceHistoryJpaRepository) {
        this.userBalanceHistoryJpaRepository = userBalanceHistoryJpaRepository;
    }

    @Override
    public List<UserBalanceHistory> findAllByUserId(Long userId) {
        return userBalanceHistoryJpaRepository.findAllByUserId(userId);
    }

    @Override
    public UserBalanceHistory save(UserBalanceHistory userBalanceHistory) {
        return userBalanceHistoryJpaRepository.save(userBalanceHistory);
    }
}
