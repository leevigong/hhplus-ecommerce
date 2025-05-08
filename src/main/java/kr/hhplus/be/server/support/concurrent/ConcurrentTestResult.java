package kr.hhplus.be.server.support.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConcurrentTestResult {

    private AtomicInteger successCount;
    private AtomicInteger failureCount;
    List<Throwable> errors;

    private ConcurrentTestResult(AtomicInteger successCount, AtomicInteger failureCount, List<Throwable> errors) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.errors = errors;
    }

    public static ConcurrentTestResult of(AtomicInteger successCount, AtomicInteger failureCount, List<Throwable> errors) {
        return new ConcurrentTestResult(successCount, failureCount, errors);
    }
}
