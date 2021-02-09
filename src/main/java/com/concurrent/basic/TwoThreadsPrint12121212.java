package com.concurrent.basic;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Webb Dong
 * @description: 两个线程打印121212
 * @date 2021-01-31 3:03 PM
 */
public class TwoThreadsPrint12121212 {

    private static final int PRINT_COUNT = 200;

    private static boolean flag1 = true;

    /**
     * 方法一、使用锁的 wait 和 notify 机制
     */
    private static void method1() {
        final Object lockObj = new Object();

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT; i++) {
                synchronized (lockObj) {
                    if (!flag1) {
                        try {
                            // 睡眠自己
                            lockObj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("1");
                    flag1 = false;
                    // 唤醒对方
                    lockObj.notifyAll();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT; i++) {
                synchronized (lockObj) {
                    if (flag1) {
                        try {
                            lockObj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("2");
                    flag1 = true;
                    lockObj.notifyAll();
                }
            }
        }).start();
    }

    private static volatile boolean flag2 = true;

    /**
     * 方法二、使用 volatile 变量实现
     */
    private static void method2() {
        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT;) {
                if (flag2) {
                    System.out.println("1");
                    i++;
                    flag2 = false;
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT;) {
                if (!flag2) {
                    System.out.println("2");
                    i++;
                    flag2 = true;
                }
            }
        }).start();
    }

    /**
     * 方法三、使用信号量实现
     */
    private static void method3() {
        final Semaphore s1 = new Semaphore(1);
        final Semaphore s2 = new Semaphore(0);

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT; i++) {
                try {
                    s1.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("1");
                s2.release();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT; i++) {
                try {
                    s2.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("2");
                s1.release();
            };
        }).start();
    }

    public static void main(String[] args) {
//        method1();
//        method2();
        method3();
    }

}
