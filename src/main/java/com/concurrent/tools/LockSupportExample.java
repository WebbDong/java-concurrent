package com.concurrent.tools;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport
 *      是一个线程工具类，所有的方法都是静态方法，可以让线程在任意位置阻塞，也可以在任意位置唤醒。
 *
 * LockSupport API:
 *      void park():                                   无期限阻塞当前线程
 *      void park(Object blocker):                     无期限阻塞当前线程，同时传入 blocker 对象
 *      void parkNanos(Object blocker, long nanos):    阻塞当前线程，直到超时，同时传入 blocker 对象
 *      void parkNanos(long nanos):                    阻塞当前线程，直到超时
 *      void parkUntil(long deadline):                 阻塞当前线程，直到某个时间
 *      void parkUntil(Object blocker, long deadline): 阻塞当前线程，直到某个时间，同时传入 blocker 对象
 *      void unpark(Thread thread):                    唤醒某个线程
 *      Object getBlocker(Thread t):                   获取某个线程上设置的 blocker 对象
 */
public class LockSupportExample {

    private static void example1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            System.out.println("t1 done");
        });
        t1.start();

        TimeUnit.SECONDS.sleep(5);
        // t1 已经调用了 park() 被阻塞了，所以无法自己唤醒自己，所以 unpark 方法需要传递被唤醒的线程对象
        LockSupport.unpark(t1);
    }

    private static void example2() throws InterruptedException {
        final Object blocker = new Object();
        Thread t1 = new Thread(() -> {
            LockSupport.park(blocker);
            System.out.println("t1 done");
        });
        t1.start();

        TimeUnit.SECONDS.sleep(5);
        Object blockerObj = LockSupport.getBlocker(t1);
        System.out.println(blockerObj == blocker);
        LockSupport.unpark(t1);

        TimeUnit.SECONDS.sleep(5);
        blockerObj = LockSupport.getBlocker(t1);
        System.out.println(blockerObj);
    }

    private static void example3() {
        Thread t1 = new Thread(() -> {
            // 暂停 5 秒
            LockSupport.parkNanos(5L * 1000L * 1000L * 1000L);
            System.out.println("t1 done");
        });
        t1.start();
    }

    private static void example4() {
        Thread t1 = new Thread(() -> {
            // 暂停 5 秒
            LockSupport.parkUntil(System.currentTimeMillis() + 5000);
            System.out.println("t1 done");
        });
        t1.start();
    }

    public static void main(String[] args) throws InterruptedException {
//        example1();
//        example2();
//        example3();
        example4();
    }

}
