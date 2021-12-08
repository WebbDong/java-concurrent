package com.concurrent.tools;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于 AQS 实现一个锁
 * @author: Webb Dong
 * @date: 2021-12-08 2:57 PM
 */
public class MyLockWithAQS implements Lock {

    private Sync sync;

    public MyLockWithAQS() {
        sync = new NonFairSync();
    }

    public MyLockWithAQS(boolean isFair) {
        sync = isFair ? new FairSync() : new NonFairSync();
    }

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    /**
     * 不可重入非公平锁
     */
    static final class NonFairSync extends MyLockWithAQS.Sync {

        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, arg)) {
                return true;
            }
            return false;
        }

    }

    /**
     * 可重入公平锁
     */
    static final class FairSync extends MyLockWithAQS.Sync {

        @Override
        protected boolean tryAcquire(int arg) {
            if (getState() == 0) {
                // hasQueuedPredecessors : 判断等待队列中是否存在等待的线程
                // 当队列中存在等待的线程，就不让当前线程获取到锁，进行排队
                if (!hasQueuedPredecessors() && compareAndSetState(0, arg)) {
                    setExclusiveOwnerThread(Thread.currentThread());
                    return true;
                }
            } else if (Thread.currentThread() == getExclusiveOwnerThread()) {
                // 重入
                setState(getState() + arg);
                return true;
            }
            return false;
        }

    }

    static abstract class Sync extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryRelease(int arg) {
            // 当锁持有线程不是当前线程时抛出异常
//            if (Thread.currentThread() != getExclusiveOwnerThread()) {
//                throw new IllegalMonitorStateException();
//            }

            int newState = getState() - arg;
            setState(newState);
            return newState == 0;
        }

    }

    private static int sum = 0;

    public static void main(String[] args) throws InterruptedException {
        MyLockWithAQS lock = new MyLockWithAQS(true);
        CountDownLatch cdl = new CountDownLatch(2);
        Runnable runnable = () -> {
            try {
                lock.lock();
                for (int i = 0; i < 100000; i++) {
                    sum++;
                }
                testReentrant(lock);
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

    public static void testReentrant(MyLockWithAQS lock) {
        try {
            lock.lock();
            for (int i = 0; i < 100000; i++) {
                sum++;
            }
        } finally {
            lock.unlock();
        }
    }

}
