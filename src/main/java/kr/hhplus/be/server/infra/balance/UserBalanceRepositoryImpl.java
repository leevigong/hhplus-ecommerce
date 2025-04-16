package kr.hhplus.be.server.infra.balance;

import kr.hhplus.be.server.domain.balance.UserBalance;
import kr.hhplus.be.server.domain.balance.UserBalanceRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserBalanceRepositoryImpl implements UserBalanceRepository {

    @Override
    public UserBalance findByUserId(Long userId) {
        return null;
    }

    @Override
    public UserBalance save(UserBalance userBalance) {
        return null;
    }
}
