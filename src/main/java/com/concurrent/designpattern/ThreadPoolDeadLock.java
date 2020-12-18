package com.concurrent.designpattern;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用线程池过程中，有一种线程死锁的场景。如果提交到相同线程池的任务不是相互独立的，而是有依赖关系的，那么就有可能导致线程死锁。
 * 具体现象是应用每运行一段时间偶尔就会处于无响应的状态，监控数据看上去一切都正常，但是实际上已经不能正常工作了。
 *
 * 问题场景：
 *      将一个大型的计算任务分成两个阶段，第一个阶段的任务会等待第二阶段的子任务完成。每一个阶段都使用了线程池，
 *      而且两个阶段使用的还是同一个线程池。
 *
 * 死锁原因：
 *      线程池中的两个线程全部都阻塞在 l2.await(); 这行代码上了，也就是说，线程池里所有的线程都在等待 L2 阶段的任务执行完。
 *
 * 解决方案：
 *      最简单粗暴的办法就是将线程池的最大线程数调大，如果能够确定任务的数量不是非常多的话，这个办法也是可行的，
 *      否则这个办法就行不通了。其实这种问题通用的解决方案是为不同的任务创建不同的线程池。
 */
public class ThreadPoolDeadLock {

    public static void main(String[] args) throws Exception {
        // L1、L2阶段共用的线程池，实际项目中不要使用Executors工具类
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch cdl1 = new CountDownLatch(2);
        for (int i = 0; i < 2; i++) {
            System.out.println("L1");
            // 执行L1阶段任务
            executorService.execute(() -> {
                CountDownLatch cdl2 = new CountDownLatch(2);
                // 执行L2阶段子任务
                for (int j = 0; j < 2; j++) {
                    executorService.execute(() -> {
                        System.out.println("L2");
                        cdl2.countDown();
                    });
                }

                try {
                    // 等待L2阶段任务执行完
                    cdl2.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                cdl1.countDown();
            });
        }
        // 等待L1阶段任务执行完
        cdl1.await();
        // 线程死锁，永远无法执行
        System.out.println("end");
        executorService.shutdown();
    }

}
