package com.concurrent.designpattern;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Webb Dong
 * @description: 生产者消费者模式
 * @date 2021-01-31 15:34
 */
public class ProducerAndConsumer {

    private static final int MAX_PRODUCTION_COUNT = 40;

    private static BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(MAX_PRODUCTION_COUNT);

    private static LongAdder longAdder = new LongAdder();

    private static ThreadPoolExecutor createProducerPool() {
        // 生产者线程池
        final ThreadPoolExecutor producerPool = new ThreadPoolExecutor(
                5,
                16,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());
        producerPool.setThreadFactory(new ThreadFactory() {

            private int count;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, new StringBuilder("producer-pool-thread-").append(count++).toString());
            }
        });
        return producerPool;
    }

    private static ThreadPoolExecutor createConsumerPool() {
        // 消费者线程池
        final ThreadPoolExecutor consumerPool = new ThreadPoolExecutor(
                5,
                16,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());
        consumerPool.setThreadFactory(new ThreadFactory() {

            private int count;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, new StringBuilder("consumer-pool-thread-").append(count++).toString());
            }
        });
        return consumerPool;
    }

    /**
     * 生产者消费者模式的优点
     *      1、解耦，生产者和消费者没有任何依赖关系，它们彼此之间的通信只能通过任务队列，所以生产者 - 消费者模式是一个不错的解耦方案。
     *      2、支持异步，并且能够平衡生产者和消费者的速度差异。
     * @param args
     */
    public static void main(String[] args) {
        final ThreadPoolExecutor producerPool = createProducerPool();
        final ThreadPoolExecutor consumerPool = createConsumerPool();

        producerPool.execute(() -> {
            for (int i = 0; i < MAX_PRODUCTION_COUNT; i++) {
                longAdder.increment();
                try {
                    String prod = "商品" + longAdder.intValue();
                    blockingQueue.put(prod);
                    System.out.println(Thread.currentThread().getName() + ": 生产" + prod + "成功");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        consumerPool.execute(() -> {
            for (int i = 0; i < MAX_PRODUCTION_COUNT; i++) {
                String prod;
                try {
                    prod = blockingQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + ": 消费" + prod + "成功");
            }
        });

        producerPool.shutdown();
        consumerPool.shutdown();
    }

}
