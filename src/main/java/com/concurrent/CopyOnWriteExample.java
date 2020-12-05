package com.concurrent;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * CopyOnWrite
 *      CopyOnWriteArrayList
 *      CopyOnWriteArraySet
 *
 *      读取数据时读原数据，而写的时候会将数据复制出一个副本进行写操作，写完之后用这个
 *      副本数据去替换掉原来的数据。写操作会加锁，读操作没有锁。
 */
public class CopyOnWriteExample {

    private static int num = 0;

    public static void main(String[] args) throws Exception {
        final List<Integer> integerList = new CopyOnWriteArrayList();
        final CountDownLatch cdl = new CountDownLatch(100);
        ThreadPoolExecutor writeThreadPool = new ThreadPoolExecutor(
                40,
                50,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());

        ThreadPoolExecutor readThreadPool = new ThreadPoolExecutor(
                40,
                50,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());

        Runnable writeRunnable = () -> {
            integerList.add(num++);
            cdl.countDown();
            System.out.println(Thread.currentThread().getName() + " write success");
        };
        Runnable readRunnable = () -> {
            System.out.println(Thread.currentThread().getName() + " " + integerList);
            cdl.countDown();
        };

        for (int i = 0; i < 50; i++) {
            writeThreadPool.execute(writeRunnable);
        }

        for (int i = 0; i < 50; i++) {
            readThreadPool.execute(readRunnable);
        }

        writeThreadPool.shutdown();
        readThreadPool.shutdown();

        cdl.await();
        System.out.println(Thread.currentThread().getName() + " " + integerList);
    }

}
