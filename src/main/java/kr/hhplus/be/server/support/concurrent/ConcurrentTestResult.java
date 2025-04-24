package kr.hhplus.be.server.support.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConcurrentTestResult {

    private AtomicInteger successCount;
    private AtomicInteger failureCount;

    private ConcurrentTestResult(AtomicInteger successCount, AtomicInteger failureCount) {
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    public static ConcurrentTestResult of(AtomicInteger successCount, AtomicInteger failureCount) {
        return new ConcurrentTestResult(successCount, failureCount);
    }
}
