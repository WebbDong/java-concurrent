package com.concurrent.tools;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Callable:
 *      实现Callable接口的任务线程能返回执行结果，Callable接口实现类中call()方法允许将异常向上抛出，也可以直接在内部处理(try...catch);
 *      Callable接口支持返回执行结果，此时需要调用FutureTask.get()方法实现，此方法会阻塞线程直到获取“将来”的结果，当不调用此方法时，
 *      主线程不会阻塞
 *
 * Runnable 和 Callable 的区别
 *      1、实现Callable接口的任务线程能返回执行结果，而实现Runnable接口的任务线程不能返回执行结果
 *      2、Callable接口实现类中call()方法允许将异常向上抛出，也可以直接在内部处理(try...catch);
 *         而Runnable接口实现类中run()方法的异常必须在内部处理掉，不能向上抛出
 */
public class CallableRunnableAndFuture {

    @SneakyThrows
    private static void callableAndFuture() {
        Callable<Integer> callable = () -> {
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                System.out.print(i + " ");
                TimeUnit.MILLISECONDS.sleep(300);
                sum += i;
            }
            return sum;
        };

        FutureTask<Integer> task = new FutureTask<>(callable);
        Thread thread = new Thread(task, "thread-1");
        thread.start();

        System.out.println("main");

        // 此处会阻塞，直到Callable执行完，然后返回结果
        Integer sum = task.get();
        System.out.println("\nsum = " + sum);
    }

    @SneakyThrows
    private static void runnableAndFuture() {
        Runnable runnable = () -> {
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                System.out.print(i + " ");
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sum += i;
            }
            System.out.println("sum = " + sum);
        };

        // 使用 Runnable 只能指定一个固定的返回值
        FutureTask<Integer> task = new FutureTask<>(runnable, 80000);
        Thread thread = new Thread(task, "thread-1");
        thread.start();

        Integer res = task.get();
        System.out.println("res = " + res);
    }

    public static void main(String[] args) throws Exception {
//        callableAndFuture();
        runnableAndFuture();
    }

}
