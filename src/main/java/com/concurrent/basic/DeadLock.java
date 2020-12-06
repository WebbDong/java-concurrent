package com.concurrent.basic;

import com.concurrent.model.Account;

import java.util.concurrent.CountDownLatch;

/**
 * 死锁
 *      发生死锁的四个条件：
 *          1、互斥，共享资源 X 和 Y 只能被一个线程占用；
 *          2、占有且等待，线程 T1 已经取得共享资源 X，在等待共享资源 Y 的时候，不释放共享资源 X；
 *          3、不可抢占，其他线程不能强行抢占线程 T1 占有的资源；
 *          4、循环等待，线程 T1 等待线程 T2 占有的资源，线程 T2 等待线程 T1 占有的资源，就是循环等待。
 *
 *      也就是说只要我们破坏其中一个，就可以成功避免死锁的发生。其中互斥没法破坏，使用锁的目的就是为了互斥，所以只能破坏其他条件。
 */
public class DeadLock {

    public static void main(String[] args) throws Exception {
        Account a1 = new Account(1L, 500000);
        Account a2 = new Account(2L, 500000);
        final int THREAD_SIZE = 10;
        final int HALF_THREAD_SIZE = THREAD_SIZE / 2;
        for (int z = 0; z < 100; z++) {
            CountDownLatch cdl = new CountDownLatch(THREAD_SIZE);
            Thread[] threads = new Thread[THREAD_SIZE];
            for (int i = 0; i < HALF_THREAD_SIZE; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 500; j++) {
//                        a1.transferThreadUnsafe(a2, 100);
                        a1.transferDeadLock(a2, 100);
                    }
                    cdl.countDown();
                });
            }
            for (int i = 0; i < HALF_THREAD_SIZE; i++) {
                threads[i + HALF_THREAD_SIZE] = new Thread(() -> {
                    for (int j = 0; j < 500; j++) {
//                        a2.transferThreadUnsafe(a1, 100);
                        a2.transferDeadLock(a1, 100);
                    }
                    cdl.countDown();
                });
            }
            for (int i = 0; i < THREAD_SIZE; i++) {
                threads[i].start();
            }
            cdl.await();
            System.out.println("a1.balance = " + a1.getBalance() + ", a2.balance = "
                    + a2.getBalance());
        }
    }

}
