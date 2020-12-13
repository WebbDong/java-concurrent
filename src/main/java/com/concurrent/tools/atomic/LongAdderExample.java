package com.concurrent.tools.atomic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 原子化的累加器
 */
public class LongAdderExample {

    private static LongAdder longAdder = new LongAdder();

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(10);
        var threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());
        final Runnable runnable = () -> {
            for (int i = 0; i < 1000; i++) {
                longAdder.increment();
            }
            cdl.countDown();
        };
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(runnable);
        }
        cdl.await();
        threadPoolExecutor.shutdown();
        System.out.println(longAdder.intValue());
    }

}
