package com.concurrent.basic;

import java.util.concurrent.CountDownLatch;

/**
 * 互斥锁
 *      synchronized可以解决原子性、可见性、有序性
 *
 *      synchronized的操作：
 *          1、获得同步锁
 *          2、清空工作内存
 *          3、从主内存中拷贝对象副本到本地内存
 *          4、执行编写的代码
 *          5、刷新主内存数据
 *          6、释放同步锁
 */
public class Synchronized {

    private static int num = 0;

    private synchronized static int get() {
        return num;
    }

    private synchronized static void write() {
        num++;
    }

    private static void sync() {
        synchronized (Synchronized.class) {
            num++;
        }
    }

    public static void main(String[] args) throws Exception {
        CountDownLatch cdl = new CountDownLatch(10);
        final int THREAD_SIZE = 10;
        Thread[] threads = new Thread[THREAD_SIZE];
        for (int i = 0; i < THREAD_SIZE; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
//                    write();
                    sync();
                }
                cdl.countDown();
            });
        }
        for (int i = 0; i < THREAD_SIZE; i++) {
            threads[i].start();
        }
        cdl.await();

        int res = get();
        System.out.println(res);
    }

}
