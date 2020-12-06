package com.concurrent.basic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java线程状态
 *      NEW（初始化状态）
 *      RUNNABLE（可运行 / 运行状态）
 *      BLOCKED（阻塞状态）
 *      WAITING（无时限等待）
 *      TIMED_WAITING（有时限等待）
 *      TERMINATED（终止状态）
 */
public class ThreadState {

    public static void main(String[] args) throws Exception {
//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
//        test6();
//        test7();
        test8();
    }

    /**
     * 线程执行完 run() 方法后，会自动转换到 TERMINATED 状态，当然如果执行
     *  run() 方法的时候异常抛出，也会导致线程终止。
     * @throws InterruptedException
     */
    public static void test8() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            System.out.println("thread1");
        });
        thread1.start();
        while (true) {
            System.out.println("thread1.getState() = " + thread1.getState());
            Thread.sleep(1000);
        }
    }

    /**
     * RUNNABLE 与 TIMED_WAITING 的状态转换，有五种场景会触发这种转换：
     *      1、调用带超时参数的 Thread.sleep(long millis) 方法；
     *      2、获得 synchronized 隐式锁的线程，调用带超时参数的 Object.wait(long timeout) 方法；
     *      3、调用带超时参数的 Thread.join(long millis) 方法；
     *      4、调用带超时参数的 LockSupport.parkNanos(Object blocker, long deadline) 方法；
     *      5、调用带超时参数的 LockSupport.parkUntil(long deadline) 方法。
     */
    public static void test7() throws InterruptedException {
        Thread thread1 = new Thread(new MyRunnable(Thread.currentThread()));
        thread1.start();
        thread1.join(5000);
        while (true) {
            System.out.println("thread1.getState() = " + thread1.getState());
            System.out.println("Thread.currentThread().getState() = "
                    + Thread.currentThread().getState() + " mainThreadId = " + Thread.currentThread().getId());
            Thread.sleep(1000);
        }
    }

    /**
     * 调用 LockSupport.park() 方法，当前线程会阻塞，线程的状态会从 RUNNABLE 转换到 WAITING。
     * 调用 LockSupport.unpark(Thread thread) 可唤醒目标线程，目标线程的状态又会从 WAITING 状态转换到 RUNNABLE。
     */
    public static void test6() throws InterruptedException {
        Runnable runnable = () -> {
            LockSupport.park();
            while (true)
                ;
        };
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();
        while (true) {
            System.out.println(Thread.currentThread().getName() + " thread1.getState() = " + thread1.getState());
            System.out.println(Thread.currentThread().getName() + " thread2.getState() = " + thread2.getState());
            Thread.sleep(1000);
        }
    }

    static class MyRunnable implements Runnable {

        private Thread thread;

        public MyRunnable(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println("main thread state = " + thread.getState() + " mainThreadId = " + thread.getId());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * 当某个线程调用了无参数的 Thread.join() 方法时，调用者的线程会阻塞，状态会转换成 WAITING
     * @throws InterruptedException
     */
    public static void test5() throws InterruptedException {
        Thread thread1 = new Thread(new MyRunnable(Thread.currentThread()));
        thread1.start();
        thread1.join();
        while (true) {
            System.out.println(Thread.currentThread().getName() + " thread1.getState() = " + thread1.getState());
        }
    }

    /**
     * ReentrantLock阻塞时线程状态会转换成 WAITING
     */
    public static void test4() throws InterruptedException {
        final Lock lock = new ReentrantLock();
        Runnable runnable = () -> {
            try {
                lock.lock();
                while (true)
                    ;
            } finally {
                lock.unlock();
            }
        };
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();
        while (true) {
            System.out.println(Thread.currentThread().getName() + " thread1.getState() = " + thread1.getState());
            System.out.println(Thread.currentThread().getName() + " thread2.getState() = " + thread2.getState());
            Thread.sleep(1000);
        }
    }

    /**
     * 只有一种场景状态会转换成BLOCKED，就是线程等待 synchronized 的隐式锁。
     */
    private static void test3() throws InterruptedException {
        final Object lockObj = new Object();
        Runnable runnable = () -> {
            synchronized (lockObj) {
                while (true)
                    ;
            }
        };
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();
        while (true) {
            System.out.println(Thread.currentThread().getName() + " thread1.getState() = " + thread1.getState());
            System.out.println(Thread.currentThread().getName() + " thread2.getState() = " + thread2.getState());
            Thread.sleep(1000);
        }
    }

    /**
     * ServerSocket.accept是阻塞式API，但是Java线程状态不会因为阻塞而改变，依然是RUNNABLE
     * 系统级别阻塞，对于JVM来说，等待 CPU 使用权（操作系统层面此时处于可执行状态）与等待 I/O（操作系统层面此时处于
     * 休眠状态）没有区别，都是在等待某个资源，所以都归入了 RUNNABLE 状态。
     */
    public static void test2() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " 启动socket监听");
                ServerSocket serverSocket = new ServerSocket(25000);
                // 监听来自客户端的连接
                Socket socket = serverSocket.accept();
                System.out.println(Thread.currentThread().getName() + " 收到客户端连接");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread1.start();
        while (true) {
            System.out.println(Thread.currentThread().getName() + " thread1.getState() = " + thread1.getState());
            Thread.sleep(1000);
        }
    }

    /**
     * 新创建的Thread对象线程状态为NEW
     */
    private static void test1() {
        Thread thread1 = new Thread();
        System.out.println("thread1.getState() = " + thread1.getState());
    }

}
