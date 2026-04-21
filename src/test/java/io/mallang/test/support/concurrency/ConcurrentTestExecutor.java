package io.mallang.test.support.concurrency;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public final class ConcurrentTestExecutor {

    private static final int TIMEOUT_SECONDS = 10;

    private ConcurrentTestExecutor() {
    }

    public static void executeConcurrently(int requestCount, Runnable task) throws InterruptedException {
        assertThat(executeConcurrentlyAndCollectFailures(requestCount, task)).isEmpty();
    }

    public static <T> void executeConcurrently(List<T> items, Consumer<T> task) throws InterruptedException {
        assertThat(executeConcurrentlyAndCollectFailures(items, task)).isEmpty();
    }

    public static void executeConcurrently(int requestCount, IntConsumer task) throws InterruptedException {
        assertThat(executeConcurrentlyAndCollectFailures(requestCount, task)).isEmpty();
    }

    public static Queue<Throwable> executeConcurrentlyAndCollectFailures(
            int requestCount,
            Runnable task
    ) throws InterruptedException {
        return executeConcurrentlyAndCollectFailures(requestCount, ignored -> task.run());
    }

    public static <T> Queue<Throwable> executeConcurrentlyAndCollectFailures(
            List<T> items,
            Consumer<T> task
    ) throws InterruptedException {
        return executeConcurrentlyAndCollectFailures(items.size(), index -> task.accept(items.get(index)));
    }

    public static Queue<Throwable> executeConcurrentlyAndCollectFailures(
            int requestCount,
            IntConsumer task
    ) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(requestCount);
        Queue<Throwable> failures = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < requestCount; i++) {
                final int index = i;

                executorService.execute(() -> {
                    try {
                        startLatch.await();
                        task.accept(index);
                    } catch (Throwable throwable) {
                        failures.add(throwable);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();

            assertThat(doneLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();
            return failures;
        } finally {
            executorService.shutdownNow();
            executorService.close();
        }
    }
}
