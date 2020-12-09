package com.concurrent.tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * ReadWriteLock 支持两种模式：一种是读锁，一种是写锁。而 StampedLock 支持三种模式，分别是：写锁、悲观读锁和乐观读。
 * 其中，写锁、悲观读锁的语义和 ReadWriteLock 的写锁、读锁的语义非常类似，允许多个线程同时获取悲观读锁，但是只允许一个线程获取写锁，
 * 写锁和悲观读锁是互斥的。不同的是：StampedLock 里的写锁和悲观读锁加锁成功之后，都会返回一个 stamp；然后解锁的时候，需要传入这个 stamp。
 *
 * 乐观读这个操作是无锁的，所以相比较 ReadWriteLock 的读锁，乐观读的性能更好一些。
 *
 * stamp可以理解为关系型数据库乐观锁的version
 */
public class StampedLockExample {

    private static final StampedLock STAMPED_LOCK = new StampedLock();

    private static int num;

    public static void main(String[] args) {
        final int WRITE_THREAD_COUNT = 50;
        final int READ_THREAD_COUNT = 80;
        Runnable writeRunnable = () -> {
            // 写锁
            long stamp = STAMPED_LOCK.writeLock();
            try {
                for (int i = 0; i < 10; i++) {
                    num++;
//                    TimeUnit.MILLISECONDS.sleep(20 * i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                STAMPED_LOCK.unlockWrite(stamp);
            }
        };
        Runnable readRunnable = () -> {
            // 乐观读
            long stamp = STAMPED_LOCK.tryOptimisticRead();
            // 读入局部变量，读的过程数据可能被修改
            int curNum = num;
            // 判断执行读操作期间，是否存在写操作，如果存在，则sl.validate返回false
            if (!STAMPED_LOCK.validate(stamp)) {
                // 升级为悲观读锁
                stamp = STAMPED_LOCK.readLock();
                try {
                    curNum = num;
                } finally {
                    // 释放悲观读锁
                    STAMPED_LOCK.unlockRead(stamp);
                }
            }
            System.out.println("num = " + curNum);
        };

        ThreadPoolExecutor threadPoolExecutor1 = new ThreadPoolExecutor(
                5,
                WRITE_THREAD_COUNT,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());
        ThreadPoolExecutor threadPoolExecutor2 = new ThreadPoolExecutor(
                5,
                READ_THREAD_COUNT,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i < WRITE_THREAD_COUNT; i++) {
            threadPoolExecutor1.execute(writeRunnable);
        }
        for (int i = 0; i < READ_THREAD_COUNT; i++) {
            threadPoolExecutor2.execute(readRunnable);
        }
        threadPoolExecutor1.shutdown();
        threadPoolExecutor2.shutdown();
    }

}
