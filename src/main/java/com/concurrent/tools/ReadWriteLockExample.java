package com.concurrent.tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock读写锁
 *      读写锁有两个锁，读锁(共享锁)和写锁(排他锁、互斥锁)。允许多个线程同时读共享变量，
 *      但只允许一个线程写共享变量，当写共享变量时也会阻塞读的操作。这样在只有读的时候就
 *      不会互斥，提高读的效率。
 *
 *      ReadWriteLock默认也是非公平锁
 */
public class ReadWriteLockExample {

    private static final ReadWriteLock RWL = new ReentrantReadWriteLock();

    private static final Lock WRITE_LOCK = RWL.writeLock();

    private static final Lock READ_LOCK = RWL.readLock();

    private static int num = 0;

    public static void main(String[] args) {
        final int WRITE_THREAD_COUNT = 50;
        final int READ_THREAD_COUNT = 80;
        final Runnable writeRunnable = () -> {
            try {
                WRITE_LOCK.lock();
                for (int i = 0; i < 10; i++) {
                    num++;
//                    TimeUnit.MILLISECONDS.sleep(20 * i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                WRITE_LOCK.unlock();
            }
        };
        var threadPoolExecutor1 = new ThreadPoolExecutor(
                5,
                WRITE_THREAD_COUNT,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());

        final Runnable readRunnable = () -> {
            try {
                READ_LOCK.lock();
                System.out.println(Thread.currentThread().getName() + " num = " + num);
            } finally {
                READ_LOCK.unlock();
            }
        };
        var threadPoolExecutor2 = new ThreadPoolExecutor(
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
