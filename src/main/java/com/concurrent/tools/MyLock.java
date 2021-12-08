package com.concurrent.tools;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * 自己实现一个简单的锁
 * @author: Webb Dong
 * @date: 2021-12-08 11:56 AM
 */
public class MyLock {

    /**
     * 锁状态，true : 锁被持有，false : 锁未被持有
     */
    private AtomicBoolean lockStatus = new AtomicBoolean(false);

    /**
     * 线程等待队列
     */
    private Queue<Thread> queue = new LinkedBlockingQueue<>();

    public void lock() {
        // CAS 获取锁
        while (!lockStatus.compareAndSet(false, true)) {
            // 如果获取不到锁，将当前线程放入等待队列，并且休眠当前的线程
            queue.offer(Thread.currentThread());
            LockSupport.park();
        }
    }

    public void unlock() {
        lockStatus.set(false);
        LockSupport.unpark(queue.poll());
    }

    private static int sum = 0;

    public static void main(String[] args) throws InterruptedException {
        MyLock lock = new MyLock();
        CountDownLatch cdl = new CountDownLatch(2);
        Runnable runnable = () -> {
            try {
                lock.lock();
                for (int i = 0; i < 100000; i++) {
                    sum++;
                }
            } finally {
                lock.unlock();
            }
            cdl.countDown();
        };
        Thread t1 = new Thread(runnable);
        t1.start();
        Thread t2 = new Thread(runnable);
        t2.start();
        cdl.await();
        System.out.println("sum = " + sum);
    }

}
