package kr.hhplus.be.server.infra.balance;

import kr.hhplus.be.server.domain.balance.UserBalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBalanceHistoryJpaRepository extends JpaRepository<UserBalanceHistory, Long> {

    List<UserBalanceHistory> findAllByUserId(Long userId);
}
