package kr.hhplus.be.server.infra.lock;

import kr.hhplus.be.server.support.lock.LockCallback;
import kr.hhplus.be.server.support.lock.LockStrategy;
import kr.hhplus.be.server.support.lock.LockTemplate;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SpinLockTemplate implements LockTemplate {

    private final RedissonClient redisson;

    public SpinLockTemplate(RedissonClient redisson) {
        this.redisson = redisson;
    }

    @Override
    public LockStrategy getLockStrategy() {
        return LockStrategy.SPIN_LOCK;
    }

    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit unit, LockCallback<T> callback) throws Throwable {
        RLock lock = redisson.getSpinLock(key);
        log.debug("SPIN 락 시도: {}", key);

        lock.lock();
        try {
            return callback.doInLock();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("SPIN 락 해제: {}", key);
            }
        }
    }
}
