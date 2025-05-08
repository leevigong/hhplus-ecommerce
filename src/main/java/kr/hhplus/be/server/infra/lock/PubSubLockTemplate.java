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
public class PubSubLockTemplate implements LockTemplate {

    private final RedissonClient redisson;

    public PubSubLockTemplate(RedissonClient redisson) {
        this.redisson = redisson;
    }

    @Override
    public LockStrategy getLockStrategy() {
        return LockStrategy.PUB_SUB_LOCK;
    }

    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) throws Throwable {
        RLock lock = redisson.getLock(key);
        log.debug("Pub/Sub 락 시도: {}", key);

        boolean acquired = lock.tryLock(waitTime, leaseTime, timeUnit);
        if (!acquired) {
            throw new IllegalStateException("락 획득 실패: " + key);
        }
        try {
            return callback.doInLock();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("Pub/Sub 락 해제: {}", key);
            }
        }
    }
}
