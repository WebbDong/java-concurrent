package com.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发原子性
 */
public class ThreadAtomicity {

    /*
    private static int num = 0;

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
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    increase();
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

//        System.out.println(num);
        System.out.println(num.get());
    }

}
