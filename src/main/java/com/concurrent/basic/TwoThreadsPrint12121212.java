package com.concurrent.basic;

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

    /**
     * 方法一、使用锁的 wait 和 notify 机制
     */
    private static void method1() {
        final Object lockObj = new Object();

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT; i++) {
                synchronized (lockObj) {
                    System.out.println("1");
                    // 唤醒对方
                    lockObj.notifyAll();
                    try {
                        // 睡眠自己
                        lockObj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 唤醒所有线程，防止最后线程永久等待
                    lockObj.notifyAll();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT; i++) {
                synchronized (lockObj) {
                    System.out.println("2");
                    lockObj.notifyAll();
                    try {
                        lockObj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lockObj.notifyAll();
                }
            }
        }).start();
    }

    private static volatile boolean flag = true;

    /**
     * 方法二、使用 volatile 变量实现
     */
    private static void method2() {
        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT;) {
                if (flag) {
                    System.out.println("1");
                    i++;
                    flag = false;
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT;) {
                if (!flag) {
                    System.out.println("2");
                    i++;
                    flag = true;
                }
            }
        }).start();
    }

    /**
     * 方法三、使用 Lock 和 Condition 实现
     */
    private static void method3() {
        final Lock lock = new ReentrantLock();
        Condition condition1 = lock.newCondition();
        Condition condition2 = lock.newCondition();

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT; i++) {
                lock.lock();
                try {
                    System.out.println("1");
                    // 唤醒对方
                    condition2.signalAll();
                    // 睡眠自己
                    condition1.await(200, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < PRINT_COUNT; i++) {
                lock.lock();
                try {
                    System.out.println("2");
                    // 唤醒对方
                    condition1.signalAll();
                    // 睡眠自己
                    condition2.await(200, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            };
        }).start();
    }

    public static void main(String[] args) {
//        method1();
//        method2();
        method3();
    }

}
