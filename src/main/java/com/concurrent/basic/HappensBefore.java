package com.concurrent.basic;

import java.util.concurrent.CountDownLatch;

/**
 * Happens-Before 规则
 *      Happens-Before 约束了编译器的优化行为
 *          1、程序的顺序性规则
 *              这条规则是指在一个线程中，按照程序顺序，前面的操作 Happens-Before 于后续的任意操作。
 *          2、volatile 变量规则
 *              对一个 volatile 变量的写操作， Happens-Before 于后续对这个 volatile 变量的读操作。
 *          3、传递性
 *              如果 A Happens-Before B，且 B Happens-Before C，那么 A Happens-Before C。
 *          4、管程中锁的规则
 *              对一个锁的解锁操作，happens-before后续对这个锁的加锁操作。
 *          5、线程 start() 规则
 *              这条是关于线程启动的。它指线程A启动子线程B后，子线程B能够看到线程A在启
 *              动子线程B前的操作。换句话说，如果线程A调用线程B的start()方法，那么该start()操作
 *              Happens-Before于线程B中的任何操作。
 *          6、线程 join() 规则
 *              这条是关于线程等待的。指线程A等待子线程B完成(线程A调用子线程B的join()方法)，当子线程B
 *              完成后(主线程A中join方法返回)，主线程能够看到子线程对共享变量的操作结果。换句话说，
 *              如果线程A中调用线程B的join()方法并成功返回，那么线程B中任何操作Happens-Before于该
 *              join()方法的返回。
 */
public class HappensBefore {

    private static int num = 0;

    private static volatile boolean v = false;

    public static void main(String[] args) throws Exception {
        CountDownLatch cdl = new CountDownLatch(2);

        // 顺序规则、volatile变量规则，传递性规则
        /*
        new Thread(() -> {
            while (num != 500 && !v)
                ;
            cdl.countDown();
            System.out.println("Thread2 finished");
        }).start();

        Thread.sleep(500);

        new Thread(() -> {
            num = 500;
            v = true;
            cdl.countDown();
        }).start();
         */

        // 锁规则
        /*
        new Thread(() -> {
            while (num != 3) {
                synchronized (HappensBeforeMain.class) {
                }
            }
            cdl.countDown();
        }).start();

        Thread.sleep(500);

        new Thread(() -> {
            num = 3;
            cdl.countDown();
        }).start();
        */

        // join规则
        /*
        Thread t2 = new Thread(() -> {
            num = 3;
            cdl.countDown();
        });
        t2.start();
        t2.join();
        System.out.println("num = " + num);
        cdl.countDown();
         */

        // start()规则
        Thread t1 = new Thread(() -> {
            while (num != 800)
                ;
            System.out.println("Thread1 finished");
            cdl.countDown();
        });
        num = 800;
        t1.start();
        cdl.countDown();

        cdl.await();
    }

}
