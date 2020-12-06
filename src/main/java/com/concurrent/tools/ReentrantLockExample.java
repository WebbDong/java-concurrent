package com.concurrent.tools;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock（可重入锁）
 */
public class ReentrantLockExample {

    private static final Lock lock = new ReentrantLock();

    private static int num = 0;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(2);
        Runnable runnable = () -> {
            if (lock.tryLock()) {
                try {
                    for (int i = 0; i < 5000; i++) {
                        num++;
                    }
                } finally {
                    lock.unlock();
                }
            }
            cdl.countDown();
        };
        new Thread(runnable).start();
        new Thread(runnable).start();
        cdl.await();
        System.out.println("num = " + num);
    }

}
