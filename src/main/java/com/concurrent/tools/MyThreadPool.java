package com.concurrent.tools;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 简版线程池
 *      目前业界线程池的设计，普遍采用的都是生产者 - 消费者模式，Java线程池也是。
 *      线程池的使用方是生产者，线程池本身是消费者。
 */
public class MyThreadPool {

    /**
     * 利用阻塞队列实现生产者-消费者模式
     */
    private BlockingQueue<Runnable> workQueue;

    private List<WorkerThread> threadList;

    public MyThreadPool(int poolSize, BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        this.threadList = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            WorkerThread workThread = new WorkerThread();
            workThread.start();
            threadList.add(workThread);
        }
    }

    /**
     * 提交任务
     * @param command
     * @throws InterruptedException
     */
    public void execute(Runnable command) throws InterruptedException {
        workQueue.put(command);
    }

    /**
     * 工作线程负责消费任务，并执行任务
     */
    private class WorkerThread extends Thread {
        @SneakyThrows
        @Override
        public void run() {
            while (true) {
                final Runnable task = workQueue.take();
                task.run();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(2);
        MyThreadPool pool = new MyThreadPool(10, workQueue);
        final Runnable command = () -> {
            System.out.println(Thread.currentThread().getName());
        };
        for (int i = 0; i < 10; i++) {
            pool.execute(command);
        }
    }

}
