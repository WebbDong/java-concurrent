package com.concurrent.tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 模拟CAS与CAS+自旋保证原子性
 */
public class SimulatedCAS {

    private static int num;

    private static void addOne() {
        int newValue;
        int oldValue;
        do {
            oldValue = num;
            newValue = oldValue + 1;
            // 如果oldValue与cas返回的值不同，那说明num的值已经被其他线程改变了，那就自旋
        } while (oldValue != cas(oldValue, newValue));
    }

    /**
     * 用synchronized模拟CAS的原子操作
     * @param expect
     * @param newValue
     * @return
     */
    private static synchronized int cas(int expect, int newValue) {
        // 读取当前num的值
        int curValue = num;
        // 比较目前num值是否等于期望值
        if (curValue == expect) {
            // 如果相同，就更新num的值
            num = newValue;
        }
        // 返回写入前的值
        return curValue;
    }

    public static void main(String[] args) throws Exception {
        final int THREAD_COUNT = 10;
        final CountDownLatch cdl = new CountDownLatch(THREAD_COUNT);
        var threadPoolExecutor1 = new ThreadPoolExecutor(
                5,
                THREAD_COUNT,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());
        Runnable runnable = () -> {
            for (int j = 0; j < 1000; j++) {
                addOne();
            }
            cdl.countDown();
        };
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPoolExecutor1.execute(runnable);
        }
        threadPoolExecutor1.shutdown();
        cdl.await();
        System.out.println(num);
    }

}
