package com.concurrent.basic;

import com.concurrent.model.Account;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 保护有关联关系的多个资源
 *      如果资源之间没有关系，每个资源一把锁就可以。如果资源之间有关系，就要选择一个粒度更大的锁，这个锁要能够覆盖所有相关的资源。
 *      除此之外，还要梳理出有哪些访问路径，所有的访问路径都要设置合适的锁。
 */
public class LockMultiResources {

    public static void main(String[] args) throws InterruptedException {
        Account a1 = new Account(500000);
        Account a2 = new Account(500000);
        Account a3 = new Account(500000);
        final int THREAD_SIZE = 10;
        final int HALF_THREAD_SIZE = THREAD_SIZE / 2;
        CountDownLatch cdl = new CountDownLatch(THREAD_SIZE);
        var executor1 = new ThreadPoolExecutor(
                HALF_THREAD_SIZE,
                HALF_THREAD_SIZE,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());
        var executor2 = new ThreadPoolExecutor(
                HALF_THREAD_SIZE,
                HALF_THREAD_SIZE,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < HALF_THREAD_SIZE; i++) {
            executor1.execute(() -> {
                for (int j = 0; j < 500; j++) {
                    a1.transfer(a2, 100);
                }
                cdl.countDown();
            });
        }
        for (int i = 0; i < HALF_THREAD_SIZE; i++) {
            executor2.execute(() -> {
                for (int j = 0; j < 500; j++) {
                    a2.transfer(a3, 100);
                }
                cdl.countDown();
            });
        }

        executor1.shutdown();
        executor2.shutdown();
        cdl.await();
        System.out.println("a1.balance = " + a1.getBalance() + ", a2.balance = "
                + a2.getBalance() + ", a3.balance = " + a3.getBalance());
    }

}
