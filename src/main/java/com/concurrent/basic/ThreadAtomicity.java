package com.concurrent.basic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发原子性
 */
public class ThreadAtomicity {

//    private static int num = 0;

    /*
    private static void increase() {
        // ++运算符不是一个原子操作，是三个原子操作的组合操作，这三个独立的原子操作组合了后就不是原子操作了
        num++;
    }
     */

    /*
    private synchronized static void increase() {
        num++;
    }
     */

    private static AtomicInteger num = new AtomicInteger(0);

    private static void increase() {
        num.incrementAndGet();
    }

    /**
     * 解决办法：1、increase方法加锁，2、使用AtomicInteger整型原子操作类
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
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
                increase();
            }
            cdl.countDown();
        };
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPoolExecutor1.execute(runnable);
        }
        threadPoolExecutor1.shutdown();
        cdl.await();
//        System.out.println(num);
        System.out.println(num.get());
    }

}
