package com.concurrent.tools.atomic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * 原子化的累加器
 */
public class LongAccumulatorExample {

    private static LongAccumulator longAccumulator = new LongAccumulator((a, b) -> a + b, 0);

    public static void main(String[] args) throws InterruptedException {
        final int THREAD_COUNT = 10;
        CountDownLatch cdl = new CountDownLatch(THREAD_COUNT);
        var threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());
        final Runnable runnable = () -> {
            for (int i = 0; i < 1000; i++) {
                longAccumulator.accumulate(1L);
            }
            cdl.countDown();
        };
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPoolExecutor.execute(runnable);
        }
        cdl.await();
        threadPoolExecutor.shutdown();
        System.out.println(longAccumulator.get());
    }

}
