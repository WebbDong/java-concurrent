package com.concurrent.tools;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 实现Callable接口的任务线程能返回执行结果
 * Callable接口实现类中call()方法允许将异常向上抛出，也可以直接在内部处理(try...catch);
 * Callable接口支持返回执行结果，此时需要调用FutureTask.get()方法实现，此方
 *  法会阻塞线程直到获取“将来”的结果，当不调用此方法时，主线程不会阻塞
 */
public class CallableAndFuture {

    public static void main(String[] args) throws Exception {
        Callable<Integer> callable = () -> {
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                System.out.print(i + " ");
                Thread.sleep(300);
                sum += i;
            }
            return sum;
        };

        FutureTask<Integer> task = new FutureTask<>(callable);
        Thread thread = new Thread(task);
        thread.start();

        System.out.println("main");

        // 此处会阻塞，直到Callable执行完，然后返回结果
        Integer sum = task.get();
        System.out.println("\nsum = " + sum);
    }

}
