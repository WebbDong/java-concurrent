package com.concurrent.tools.atomic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 原子化对象属性更新器
 */
public class AtomicIntegerFieldUpdaterExample {

    private static AtomicIntegerFieldUpdater fieldUpdater =
            AtomicIntegerFieldUpdater.newUpdater(AtomicIntegerFieldUpdaterExample.class, "num");

    private volatile int num;

    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerFieldUpdaterExample e = new AtomicIntegerFieldUpdaterExample();
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
                fieldUpdater.incrementAndGet(e);
            }
            cdl.countDown();
        };
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(runnable);
        }
        cdl.await();
        threadPoolExecutor.shutdown();
        System.out.println(fieldUpdater.get(e));
    }

}
