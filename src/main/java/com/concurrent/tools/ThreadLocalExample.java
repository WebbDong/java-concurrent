package com.concurrent.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private static void example1() throws Exception {
        final ThreadLocal<Integer> integerThreadLocal = new ThreadLocal<>();
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

    /**
     * SimpleDateFormat 不是线程安全的，那如果需要在并发场景下使用它，有一个办法就是用 ThreadLocal 来解决
     */
    private static class SafeDateFormat {

        private static final ThreadLocal<DateFormat> THREAD_LOCAL =
                ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        public static DateFormat get() {
            return THREAD_LOCAL.get();
        }

    }

    private static void example2() {
        var threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());
        // 打印的所有ThreadLocal中的SimpleDateFormat会看到hash地址值都一样，原因是SimpleDateFormat的toString方法
        // toString方法根据hashCode方法的返回值，pattern.hashcode(),pattern就是我们的yyyy-MM-dd HH:mm:ss，这个是一直保持不变的
        final Runnable runnable = () -> System.out.println(Thread.currentThread().getName() + "--" + SafeDateFormat.get());
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(runnable);
        }
        threadPoolExecutor.shutdown();
    }

    /**
     * InheritableThreadLocal
     *      通过 ThreadLocal 创建的线程变量，其子线程是无法继承的。也就是说你在线程中通过 ThreadLocal 创建了线程变量 V，
     *      而后该线程创建了子线程，你在子线程中是无法通过 ThreadLocal 来访问父线程的线程变量 V 的。但是使用 InheritableThreadLocal
     *      可以实现这个功能。
     *
     *      不建议在线程池中使用 InheritableThreadLocal
     */
    private static void example3() {
        ThreadLocal<Integer> tl = new InheritableThreadLocal();
        new Thread(() -> {
            tl.set(9000);
            // 在线程中创建启动另一个子线程，使用 InheritableThreadLocal 可以获取到值
            new Thread(() -> System.out.println(Thread.currentThread().getName() + "--" + tl.get())).start();
        }).start();
    }

    public static void main(String[] args) throws Exception {
//        example1();
//        example2();
        example3();
    }

}
