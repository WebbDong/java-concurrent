package com.concurrent.tools;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * 信号量
 */
public class SemaphoreExample {

    private static int count;

    private static final Semaphore semaphore = new Semaphore(1);

    public static void addOne() throws InterruptedException {
        try {
            semaphore.acquire();
            count++;
        } finally {
            semaphore.release();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(2);
        Runnable runnable = () -> {
            for (int i = 0; i < 5000; i++) {
                try {
                    addOne();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cdl.countDown();
        };
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.start();
        t2.start();
        cdl.await();
        System.out.println("count = " + count);
    }

}
