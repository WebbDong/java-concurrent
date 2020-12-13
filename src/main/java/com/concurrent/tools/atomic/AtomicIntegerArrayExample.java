package com.concurrent.tools.atomic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicIntegerArrayExample {

    private static AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(5);

    public static void main(String[] args) throws InterruptedException {
        final int THREAD_COUNT = 10;
        final CountDownLatch cdl = new CountDownLatch(THREAD_COUNT);
        var threadPoolExecutor1 = new ThreadPoolExecutor(
                5,
                THREAD_COUNT,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());
        Runnable runnable = () -> {
            for (int j = 0; j < 1000; j++) {
                atomicIntegerArray.incrementAndGet(0);
            }
            cdl.countDown();
        };
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPoolExecutor1.execute(runnable);
        }
        threadPoolExecutor1.shutdown();
        cdl.await();
        System.out.println(atomicIntegerArray.get(0));
    }

}
