package com.concurrent.tools;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CyclicBarrier
 *      是一组线程之间互相等待。当计数器为0时会调用回调函数。而且具备自动重置的功能，一旦计数器减到 0 会自动重置到你设置的初始值。
 */
public class CyclicBarrierExample {

    private static Queue<Integer> queue1 = new ArrayDeque<>(1);

    private static Queue<Integer> queue2 = new ArrayDeque<>(1);

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    private static final CyclicBarrier cyclicBarrier = new CyclicBarrier(
            2, () -> executorService.execute(() -> calc()));

//    private static final CyclicBarrier cyclicBarrier = new CyclicBarrier(
//            2, () -> calc());

    private static void calc() {
        final Integer num1 = queue1.poll();
        final Integer num2 = queue2.poll();
        System.out.println(Thread.currentThread().getName() + "： num1 + num2 = " + (num1 + num2));
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            System.out.println("Line34：" + Thread.currentThread().getName());
            try {
                for (int i = 0; i <= 50; i++) {
                    queue1.add(i);
                    // 计数器减1， 并且等待
                    cyclicBarrier.await();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            System.out.println("Line47：" + Thread.currentThread().getName());
            try {
                for (int i = 100; i <= 150; i++) {
                    queue2.add(i);
                    // 计数器减1， 并且等待
                    cyclicBarrier.await();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t2.start();
    }

}
