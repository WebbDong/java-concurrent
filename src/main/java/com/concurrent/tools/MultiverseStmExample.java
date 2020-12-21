package com.concurrent.tools;

import com.concurrent.model.AccountMultiverse;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 软件事务内存（Software Transactional Memory，简称 STM）
 * Multiverse是STM框架
 */
public class MultiverseStmExample {

    public static void main(String[] args) throws Exception {
        AccountMultiverse a1 = new AccountMultiverse(500000);
        AccountMultiverse a2 = new AccountMultiverse(500000);
        AccountMultiverse a3 = new AccountMultiverse(500000);
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
        System.out.println("a1.balance = " + a1.getBalance().atomicGet() + ", a2.balance = "
                + a2.getBalance().atomicGet() + ", a3.balance = " + a3.getBalance().atomicGet());
    }

}
