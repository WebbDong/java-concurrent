package com.concurrent.tools;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch
 *      CountDownLatch可以实现线程间的协调等待，当计数器为0时，就会结束等待。
 */
public class CountDownLatchExample {

    private static void example1() throws InterruptedException {
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

        // main 线程阻塞等待，计数器为0时继续向下执行
        cdl.await();

        System.out.println("done");
    }

    private static void example2() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    // 所有线程阻塞在这里
                    cdl.await();
                    System.out.println(Thread.currentThread().getName() + " is starting...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(2000);
        // 发号施令，所有线程继续执行
        cdl.countDown();
    }

    public static void main(String[] args) throws InterruptedException {
//        example1();
        example2();
    }

}
