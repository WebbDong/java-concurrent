package com.concurrent.basic;

/**
 * Thread.yield() : 当某个线程调用了此方法之后，会让出 CPU 时间片，让出后，其他线程会与本线程
 *      共同竞争 CPU 时间片
 */
public class ThreadYield {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            long i = 0;
            while (true) {
                System.out.println(Thread.currentThread().getName() + " : " + (++i));
            }
        });
        t1.setName("Thread-1");
        t1.start();

        Thread t2 = new Thread(() -> {
            long i = 0;
            while (true) {
                if (i == 1000) {
                    Thread.yield();
                }
                System.out.println(Thread.currentThread().getName() + " : " + (++i));
            }
        });
        t2.setName("Thread-2");
        t2.start();
    }

}
