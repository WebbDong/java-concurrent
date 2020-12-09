package com.concurrent.tools;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock（可重入锁）
 *      1、ReentrantLock默认是非公平锁，构造函数传入true为公平锁
 *          非公平锁：多个线程去获取锁的时候，不按照线程申请锁的顺序获取锁，而是随机获取锁。
 *          公平锁：多个线程按照申请锁的顺序去获取锁，线程会直接进入等待队列排队，永远都是
 *                  队列的第一位获取到锁。FIFO先进先出顺序
 *
 *      2、
 */
public class ReentrantLockExample {

    private static final Lock lock = new ReentrantLock();

    private static int num = 0;

    /**
     * 非公平锁
     */
    private static void nonFairLock() {
        final Lock l = new ReentrantLock();
        final Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName() + "启动");
            try {
                l.lock();
                System.out.println(Thread.currentThread().getName() + "获得了锁");
            } finally {
                l.unlock();
            }
        };
        final int THREAD_AMOUNT = 10;
        Thread[] threads = new Thread[THREAD_AMOUNT];
        for (int i = 0; i < THREAD_AMOUNT; i++) {
            threads[i] = new Thread(runnable);
        }
        for (int i = 0; i < THREAD_AMOUNT; i++) {
            threads[i].start();
        }
    }

    /**
     * 公平锁
     */
    private static void fairLock() {
        final Lock l = new ReentrantLock(true);
        final Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName() + "启动");
            try {
                l.lock();
                System.out.println(Thread.currentThread().getName() + "获得了锁");
            } finally {
                l.unlock();
            }
        };
        final int THREAD_AMOUNT = 10;
        Thread[] threads = new Thread[THREAD_AMOUNT];
        for (int i = 0; i < THREAD_AMOUNT; i++) {
            threads[i] = new Thread(runnable);
        }
        for (int i = 0; i < THREAD_AMOUNT; i++) {
            threads[i].start();
        }
    }

    public static void tryLock() throws InterruptedException {
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

    public static void main(String[] args) throws InterruptedException {
//        nonFairLock();
//        fairLock();
        tryLock();
    }

}
