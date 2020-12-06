package com.concurrent.basic;

/**
 * synchronized 可重入性
 */
public class SynchronizedReentrancy {

    private static int num1 = 0;

    private static int num2 = 0;

    public synchronized static void increase() {
        num2++;
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> {
            for (int i = 0; i < 10000; i++) {
                synchronized (SynchronizedReentrancy.class) {
                    num1++;
                    // 对象锁相同，可重入
                    increase();
                }
            }
        };
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("num1 = " + num1 + ", num2 = " + num2);
    }

}
