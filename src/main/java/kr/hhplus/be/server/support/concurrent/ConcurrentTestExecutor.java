package kr.hhplus.be.server.support.concurrent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentTestExecutor {

    public ConcurrentTestResult execute(int threads, int counter, List<Runnable> tasks) throws Throwable {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(counter);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        // 예외를 보관할 리스트
        CopyOnWriteArrayList<Throwable> errors = new CopyOnWriteArrayList<>();

        for (int i = 0; i < counter; i++) {
            for (Runnable task : tasks) {
                executorService.execute(() -> {
                    try {
                        task.run();
                        successCount.incrementAndGet();
                    } catch (Throwable t) {
                        failureCount.incrementAndGet();
                        errors.add(t);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();
        executorService.shutdown();

        if (!errors.isEmpty()) {
            throw errors.get(0);
        }

        return ConcurrentTestResult.of(successCount, failureCount);
    }
}
