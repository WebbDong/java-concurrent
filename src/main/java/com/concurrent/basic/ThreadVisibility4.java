package com.concurrent.basic;

import com.concurrent.model.Account;

import java.util.concurrent.TimeUnit;

/**
 * 并发可见性
 *      volatile修饰引用数据类型数组，元素的数据也解决可见性
 */
public class ThreadVisibility4 {

    private static Account[] accounts = new Account[10];

//    private static volatile Account[] accounts = new Account[10];

    static {
        accounts[0] = new Account(10);
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            System.out.println("Thread 1 waiting...");
            while (accounts[0].getBalance() == 10)
                ;
            System.out.println("Thread 1 done...");
        }).start();

        TimeUnit.MILLISECONDS.sleep(500);

        new Thread(() -> {
            System.out.println("Thread 2 started...");
            accounts[0].setBalance(20);
            System.out.println("Thread 2 done...");
        }).start();
    }

}
