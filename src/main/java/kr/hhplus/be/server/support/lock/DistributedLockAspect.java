package kr.hhplus.be.server.support.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class DistributedLockAspect {

    private final LockKeyGenerator generator;
    private final LockStrategyRegistry registry;

    @Around("@annotation(kr.hhplus.be.server.support.lock.DistributedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        DistributedLock ann = sig.getMethod().getAnnotation(DistributedLock.class);

        log.debug("[Lock] >>> AOP 진입 - method={}", sig.toShortString());

        // 키 생성
        String key = generator.generateKey(
                sig.getParameterNames(),
                joinPoint.getArgs(),
                ann.key(),
                ann.resource()
        );
        log.debug("[Lock] 키 생성 완료 - {}", key);

        // 락 템플릿 선택
        LockTemplate tpl = registry.getLockTemplate(ann.strategy());
        log.debug("[Lock] 템플릿 선택 - {}", tpl.getClass().getSimpleName());

        // 락 템플릿 실행: 락 획득 → 비즈니스 실행 → 언락
        return tpl.executeWithLock(
                key,
                ann.waitTime(),
                ann.leaseTime(),
                ann.timeUnit(),
                joinPoint::proceed
        );
    }
}
