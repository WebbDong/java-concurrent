package com.concurrent.basic;

/**
 * 并发可见性
 *      volatile修饰基本数据类型数组，元素的数据也解决可见性
 */
public class ThreadVisibility2 {

//    private static int[] arr = new int[10];

    private static volatile int[] arr = new int[10];

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            System.out.println("Thread 1 waiting...");
            while (arr[0] == 0)
                ;
            System.out.println("Thread 1 done...");
        }).start();

        Thread.sleep(500);

        new Thread(() -> {
            System.out.println("Thread 2 started...");
            setFirst(1);
            System.out.println("Thread 2 done...");
        }).start();
    }

    public static void setFirst(int n) {
        arr[0] = n;
    }

}
