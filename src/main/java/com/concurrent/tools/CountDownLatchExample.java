package com.concurrent.tools;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch
 *      CountDownLatch可以实现线程间的协调等待，当计数器为0时，就会结束等待。
 */
public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {
        final int COUNT = 100;
        final CountDownLatch cdl = new CountDownLatch(COUNT);
        final Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName() + " started...");
            int sum = 0;
            for (int i = 0, len = COUNT / 2; i < len; i++) {
                sum += i;
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 计数器减一
                cdl.countDown();
            }
            System.out.println(Thread.currentThread().getName() + ": sum = " + sum);
        };

        new Thread(runnable, "Thread-1").start();
        new Thread(runnable, "Thread-2").start();

        // 阻塞等待，计数器为0时继续向下执行
        cdl.await();

        System.out.println("done");
    }

}
