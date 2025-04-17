package kr.hhplus.be.server.infra.balance;

import kr.hhplus.be.server.domain.balance.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBalanceJpaRepository extends JpaRepository<UserBalance, Long> {

    Optional<UserBalance> findByUserId(Long userId);
}
