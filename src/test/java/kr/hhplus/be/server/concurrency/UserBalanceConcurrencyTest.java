package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.domain.balance.UserBalance;
import kr.hhplus.be.server.domain.balance.UserBalanceCommand;
import kr.hhplus.be.server.domain.balance.UserBalanceRepository;
import kr.hhplus.be.server.domain.balance.UserBalanceService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.concurrent.ConcurrentTestResult;
import kr.hhplus.be.server.support.concurrent.ConcurrentTestExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserBalanceConcurrencyTest {

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    private User user;
    private ConcurrentTestExecutor executor;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.create("닉네임1"));
        userBalanceRepository.save(UserBalance.create(user, 0));
        executor = new ConcurrentTestExecutor();
    }

    @Test
    void 유저_포인트_충전_2번_동시성_테스트() throws Throwable {
        // given
        UserBalanceCommand.Charge cmd = UserBalanceCommand.Charge.of(user.getId(), 1000);

        List<Runnable> tasks = List.of(() -> userBalanceService.charge(cmd));

        // when
        ConcurrentTestResult executed = executor.execute(2, 2, tasks);

        // then
        System.out.println("성공 카운트: " + executed.getSuccessCount().get());
        System.out.println("실패 카운트: " + executed.getFailureCount().get());

        UserBalance userBalance = userBalanceRepository.getByUserId(user.getId());
        System.out.println("최종 유저 잔액: " + userBalance.getBalance());
        assertThat(userBalance.getBalance()).isEqualTo(2000);
    }
}
