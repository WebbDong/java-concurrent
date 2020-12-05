package com.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * ThreadLocal
 *      ThreadLocal为变量在每个线程中都创建一个副本，每个线程都只能访问自己内部的副本变量。实现线程隔离。
 *
 * ThreadLocalMap的value强引用导致的内存泄漏
 *      虽然key是弱引用，解决了key的内存泄露问题，但是value是强引用，所以ThreadLocalMap不会gc回收，依然存在内存泄漏。
 *      解决办法：使用完ThreadLocal后，执行remove操作，避免出现内存溢出情况。
 *
 * ThreadLocal数据污染、脏数据
 *      由于线程池会复用Thread对象，进而Thread对象中的threalLocals也会被复用，导致Thread对象在执行其他任务时通过get()方法获取到之前任务设置的数据，从而产生脏数据。
 *      解决方法：复用线程在执行下个任务之前调用set()重新设置值，那么脏数据问题就不会出现了。
 */
public class ThreadLocalExample {

    private static ThreadLocal<Integer> integerThreadLocal = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        CountDownLatch cdl = new CountDownLatch(2);
        integerThreadLocal.set(8000);
        new Thread(() -> {
            integerThreadLocal.set(60);
            System.out.println(Thread.currentThread().getName()
                    + " integerThreadLocal.get() = " + integerThreadLocal.get());
            cdl.countDown();
        }).start();
        new Thread(() -> {
            integerThreadLocal.set(90);
            System.out.println(Thread.currentThread().getName()
                    + " integerThreadLocal.get() = " + integerThreadLocal.get());
            cdl.countDown();
        }).start();
        cdl.await();
        System.out.println(Thread.currentThread().getName()
                + " integerThreadLocal.get() = " + integerThreadLocal.get());
    }

}
