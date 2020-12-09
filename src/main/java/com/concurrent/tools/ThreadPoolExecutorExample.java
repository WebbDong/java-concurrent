package com.concurrent.tools;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 */
public class ThreadPoolExecutorExample {

    private static int num = 0;

    /**
     * 自定义拒绝策略
     */
    private static class MyRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
            new Thread(runnable, "new thread " + new Random().nextInt(threadPoolExecutor.getPoolSize())).start();
        }

    }

    public static void main(String[] args) throws Exception {
        Runnable command = () -> System.out.println(Thread.currentThread().getName() + " task execute num = " + num++);
        /*
         * corePoolSize：核心线程数
         * maximumPoolSize：最大线程数
         * keepAliveTime：空闲线程存活时间
         * unit：时间单位
         * workQueue：任务阻塞队列
         * threadFactory：线程创建工厂
         * handler：拒绝策略处理器
         *      ThreadPoolExecutor.AbortPolicy：默认的策略，多出的无法处理的任务直接抛出java.util.concurrent.RejectedExecutionException异常
         *      ThreadPoolExecutor.CallerRunsPolicy：被拒绝的任务，会再接在调用execute方法的线程中运行，如果该线程已经销毁，则丢弃该任务
         *      ThreadPoolExecutor.DiscardOldestPolicy：会丢弃任务队列中最旧的任务，也就是最先加入队列的，再把这个被拒绝的新任务添加进去
         *      ThreadPoolExecutor.DiscardPolicy：直接拒绝无法处理的任务
         */
        var threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        System.out.println("initial poolSize = " + threadPoolExecutor.getPoolSize());
        for (int i = 0; i < 1000; i++) {
            threadPoolExecutor.execute(command);
        }
/*        for (int i = 0; i < 30; i++) {
            System.out.println("poolSize = " + threadPoolExecutor.getPoolSize());
            Thread.sleep(1000);
        }*/
        System.out.println("after poolSize = " + threadPoolExecutor.getPoolSize());
        threadPoolExecutor.shutdown();
        System.out.println("Finished...");
    }

}
