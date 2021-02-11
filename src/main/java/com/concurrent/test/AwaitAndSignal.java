package com.concurrent.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试验证 await 和 signal
 */
public class AwaitAndSignal {

    private static int state = 1;

    /**
     * 两个线程，t2 signal t1，当 t2 运行结束释放锁后，t1 可以被唤醒，并且结束线程
     */
    private static void test1() {
        final Lock lock = new ReentrantLock();
        final Condition condition1 = lock.newCondition();

        new Thread(() -> {
            try {
                lock.lock();
                while (state != 0) {
                    condition1.await();
                    System.out.println(Thread.currentThread().getName() + ": woke up");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t1").start();

        new Thread(() -> {
            try {
                lock.lock();
                state = 0;
                TimeUnit.SECONDS.sleep(3);
                condition1.signal();
                System.out.println("condition1.signal()");
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }

    /**
     * 三个线程，t3 signal t1，t3 signal t2，两个 Condition，t1 使用 condition1，t2 使用 condition2。
     * 当 t3 运行结束释放锁后，t1 可以被唤醒，并且结束线程，t2 也可以被唤醒，并且结束线程
     */
    private static void test2() {
        final Lock lock = new ReentrantLock();
        final Condition condition1 = lock.newCondition();
        final Condition condition2 = lock.newCondition();

        new Thread(() -> {
            try {
                lock.lock();
                while (state != 0) {
                    condition1.await();
                    System.out.println(Thread.currentThread().getName() + ": woke up");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + " done...");
        }, "t1").start();

        new Thread(() -> {
            try {
                lock.lock();
                while (state != 0) {
                    condition2.await();
                    System.out.println(Thread.currentThread().getName() + ": woke up");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + " done...");
        }, "t2").start();

        new Thread(() -> {
            try {
                lock.lock();
                state = 0;
                TimeUnit.SECONDS.sleep(3);
                condition1.signal();
                System.out.println("condition1.signal()");
                TimeUnit.SECONDS.sleep(3);
                condition2.signal();
                System.out.println("condition2.signal()");
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + " done...");
        }, "t3").start();
    }

    public static void main(String[] args) {
//        test1();
        test2();
    }

}
