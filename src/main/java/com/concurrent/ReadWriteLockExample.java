package com.concurrent;

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

    private static int num = 0;

    public static void main(String[] args) {
        final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        final int WRITE_THREAD_AMOUNT = 10;
        final Runnable writeRunnable = () -> {
            try {
                readWriteLock.writeLock().lock();
                for (int i = 0; i < 10; i++) {
                    num++;
                }
            } finally {
                readWriteLock.writeLock().unlock();
            }
        };
        Thread[] writeThreads = new Thread[WRITE_THREAD_AMOUNT];
        for (int i = 0; i < WRITE_THREAD_AMOUNT; i++) {
            writeThreads[i] = new Thread(writeRunnable);
        }

        final int READ_THREAD_AMOUNT = 20;
        final Runnable readRunnable = () -> {
            try {
                readWriteLock.readLock().lock();
                System.out.println(Thread.currentThread().getName() + " num = " + num);
            } finally {
                readWriteLock.readLock().unlock();
            }
        };
        Thread[] readThreads = new Thread[READ_THREAD_AMOUNT];
        for (int i = 0; i < READ_THREAD_AMOUNT; i++) {
            readThreads[i] = new Thread(readRunnable);
        }

        for (int i = 0; i < WRITE_THREAD_AMOUNT; i++) {
            writeThreads[i].start();
        }
        for (int i = 0; i < READ_THREAD_AMOUNT; i++) {
            readThreads[i].start();
        }
    }

}
