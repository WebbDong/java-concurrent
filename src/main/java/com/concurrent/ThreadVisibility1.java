package com.concurrent;

/**
 * 并发可见性
 */
public class ThreadVisibility1 {

//    private static boolean flag = true;

    private static volatile boolean flag = true;

    private static void updateFlag() {
        flag = false;
    }

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            System.out.println("Thread 1 waiting...");
            while (flag)
                ;
            System.out.println("Thread 1 done...");
        }).start();

        Thread.sleep(500);

        new Thread(() -> {
            System.out.println("Thread 2 started...");
            updateFlag();
            System.out.println("Thread 2 done...");
        }).start();
    }

}
