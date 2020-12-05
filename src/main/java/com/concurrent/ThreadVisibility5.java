package com.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * 并发可见性
 *      System.out.println方法的源码使用了synchronized，所以会影响到可见性。
 */
public class ThreadVisibility5 {

    public static long[] arr = new long[20];

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            while (true) {
                // System.out.println方法内部使用了synchronized，所以可见性问题就没有了。
                    System.out.println("println");
//                test();
                if (arr[0] == 2) {
                    break;
                }
            }
            System.out.println("Jump out of the loop!");
        }).start();

        TimeUnit.MILLISECONDS.sleep(200);

        new Thread(() -> arr[0] = 2).start();
    }

    static void test() {
        synchronized (ThreadVisibility5.class) {
        }
    }


}
