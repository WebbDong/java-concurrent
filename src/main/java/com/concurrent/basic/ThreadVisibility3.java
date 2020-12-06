package com.concurrent.basic;

import com.concurrent.model.Account;

/**
 * 并发可见性
 *      volatile修饰类对象，对象的属性也解决可见性
 */
public class ThreadVisibility3 {

//    private static Account account = new Account(0);

    private static volatile Account account = new Account(0);

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            System.out.println("Thread 1 waiting...");
            while (account.getBalance() == 0)
                ;
            System.out.println("Thread 1 done...");
        }).start();

        Thread.sleep(500);

        new Thread(() -> {
            System.out.println("Thread 2 started...");
            account.setBalance(1);
            System.out.println("Thread 2 done...");
        }).start();
    }

}
