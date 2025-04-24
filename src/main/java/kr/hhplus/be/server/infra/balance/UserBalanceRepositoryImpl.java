package kr.hhplus.be.server.infra.balance;

import kr.hhplus.be.server.domain.balance.UserBalance;
import kr.hhplus.be.server.domain.balance.UserBalanceRepository;
import kr.hhplus.be.server.support.exception.ApiErrorCode;
import kr.hhplus.be.server.support.exception.ApiException;
import org.springframework.stereotype.Repository;

@Repository
public class UserBalanceRepositoryImpl implements UserBalanceRepository {

    private final UserBalanceJpaRepository userBalanceJpaRepository;

    public UserBalanceRepositoryImpl(UserBalanceJpaRepository userBalanceJpaRepository) {
        this.userBalanceJpaRepository = userBalanceJpaRepository;
    }

    @Override
    public UserBalance getByUserId(Long userId) {
        return userBalanceJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_USER));
    }

    @Override
    public UserBalance save(UserBalance userBalance) {
        return userBalanceJpaRepository.save(userBalance);
    }
}
