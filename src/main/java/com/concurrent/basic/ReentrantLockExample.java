package com.concurrent.basic;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock：
 *      1、ReentrantLock默认是非公平锁，构造函数传入true为公平锁
 *
 *      非公平锁：多个线程去获取锁的时候，不按照线程申请锁的顺序获取锁，而是随机获取锁。
 *
 *      公平锁：多个线程按照申请锁的顺序去获取锁，线程会直接进入等待队列排队，永远都是
 *              队列的第一位获取到锁。FIFO先进先出顺序
 */
public class ReentrantLockExample {

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

    public static void main(String[] args) {
//        nonFairLock();
        fairLock();
    }

}
