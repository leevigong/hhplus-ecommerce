package kr.hhplus.be.server.support.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

// 커스텀 어노테이션
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 락 적용할 리소스 종류
     */
    LockResource resource();

    /**
     * 락 키 (SpEL 가능)
     */
    String key();

    /**
     * 락 전략 타입 (기본: Pub/Sub)
     */
    LockStrategy strategy() default LockStrategy.PUB_SUB_LOCK;

    /**
     * 락 획득 대기 최대 시간 (기본: 10초)
     */
    long waitTime() default 10;

    /**
     * 락 유효 시간(기본: 30초)
     */
    long leaseTime() default 30;

    /**
     * 시간 단위 (기본: 초)
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
