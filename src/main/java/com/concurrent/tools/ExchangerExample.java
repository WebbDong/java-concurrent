package com.concurrent.tools;

import java.util.concurrent.Exchanger;

/**
 * Exchanger
 *      用于线程间互相交换数据
 */
public class ExchangerExample {

    public static void main(String[] args) {
        final Exchanger<Integer> exchanger = new Exchanger<>();

        new Thread(() -> {
            int data;
            for (int i = 1; i <= 5; i++) {
                data = i;
                System.out.println(Thread.currentThread().getName() + ": 交换前数据 = " + data);
                try {
                    data = exchanger.exchange(data);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + ": 交换后数据 = " + data);
            }
        }, "Thread-1").start();

        new Thread(() -> {
            int data;
            for (int i = 10; i < 15; i++) {
                data = i;
                System.out.println(Thread.currentThread().getName() + ": 交换前数据 = " + data);
                try {
                    data = exchanger.exchange(data);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + ": 交换后数据 = " + data);
            }
        }, "Thread-2").start();
    }

}
