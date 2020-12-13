package com.concurrent.tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * CompletionService
 *      当需要批量提交异步任务的时候建议使用 CompletionService。CompletionService 将线程池 Executor 和阻塞队列 BlockingQueue
 *      的功能融合在了一起，能够让批量异步任务的管理更简单。除此之外，CompletionService 能够让异步任务的执行结果有序化，
 *      先执行完的先进入阻塞队列，利用这个特性，你可以轻松实现后续处理的有序性，避免无谓的等待，同时还可以快速实现诸如 Forking Cluster 这样的需求。
 */
public class CompletionServiceExample {

    private static void sleep(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        var threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());

        CompletionService<Integer> cs = new ExecutorCompletionService<>(threadPoolExecutor);
        cs.submit(() -> {
            sleep(2);
            return 10;
        });
        cs.submit(() -> {
            sleep(5);
            return 100;
        });
        cs.submit(() -> {
            sleep(10);
            return 500;
        });
        for (int i = 0; i < 3; i++) {
            // 三个submit谁先完成，谁会先放入阻塞队列中，然后此处会获取
            final Integer res = cs.take().get();
            threadPoolExecutor.execute(() -> System.out.println(res));
        }

        threadPoolExecutor.shutdown();
    }

}
