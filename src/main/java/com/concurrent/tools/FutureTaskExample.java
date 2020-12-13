package com.concurrent.tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * FutureTask
 *      FutureTask 实现了 Runnable 和 Future 接口
 */
public class FutureTaskExample {

    private static void example2() throws Exception {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> 1 + 2);
        var threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.submit(futureTask);
        threadPoolExecutor.shutdown();

        System.out.println(futureTask.get());
    }

    private static void example1() throws Exception {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> 5 + 6);
        Thread t1 = new Thread(futureTask);
        t1.start();
        final Integer res = futureTask.get();
        System.out.println(res);
    }

    public static void main(String[] args) throws Exception {
//        example1();
        example2();
    }

}
