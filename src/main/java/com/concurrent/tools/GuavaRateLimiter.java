package com.concurrent.tools;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Guava RateLimiter 限流器
 *      RateLimiter.create(double permitsPerSecond)：
 *
 *      RateLimiter.create(double permitsPerSecond, Duration warmupPeriod)：
 *
 *      RateLimiter.create(double permitsPerSecond, long warmupPeriod, TimeUnit unit)：
 *
 *      RateLimiter.create(double permitsPerSecond, long warmupPeriod, TimeUnit unit, double coldFactor, SleepingStopwatch stopwatch)：
 *
 *      RateLimiter.create(double permitsPerSecond, SleepingStopwatch stopwatch)：
 */
public class GuavaRateLimiter {

    /**
     * 存储上一次执行时间
     */
    private static long prevTime;

    /**
     * 阻塞方式的限流器
     */
    private static void blockingLimiter() {
        // 限流器流速：每秒2个请求
        RateLimiter limiter = RateLimiter.create(2.0);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        // 上一次执行时间
        prevTime = System.nanoTime();
        for (int i = 0; i < 20; i++) {
            // 限流器限流，阻塞方式
            limiter.acquire();
            executorService.execute(() -> {
                long currentTime = System.nanoTime();
                // 打印时间间隔（毫秒）
                System.out.println((currentTime - prevTime) / 1000_000);
                prevTime = currentTime;
            });
        }
        executorService.shutdown();
    }

    /**
     * 非阻塞方式的限流器
     */
    private static void unblockingLimiter() {
        // 限流器流速：每秒2个请求
        RateLimiter limiter = RateLimiter.create(2.0);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        // 上一次执行时间
        prevTime = System.nanoTime();
        for (int i = 0; i < 20; i++) {
            // 限流器限流，非阻塞方式
            if (limiter.tryAcquire()) {
                executorService.execute(() -> {
                    long currentTime = System.nanoTime();
                    // 打印时间间隔（毫秒）
                    System.out.println((currentTime - prevTime) / 1000_000);
                    prevTime = currentTime;
                });
            } else {
                System.out.println("令牌不够");
            }
        }
        executorService.shutdown();
    }

    public static void main(String[] args) {
//        blockingLimiter();
        unblockingLimiter();
    }

}
