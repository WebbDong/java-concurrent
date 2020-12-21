package com.concurrent.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 令牌桶算法简易限流器
 */
public class MySimpleLimiter {

    /**
     * 当前令牌桶中的令牌数量
     */
    private long storedPermits = 0;

    /**
     * 令牌桶的容量
     */
    private long maxPermits = 3;

    /**
     * 下一令牌产生时间
     */
    private long next = System.nanoTime();

    /**
     * 发放令牌间隔：纳秒
     */
    private final long INTERVAL = 1000_000_000;

    /**
     * 申请令牌
     */
    public void acquire() {
        // 申请令牌时的时间
        long now = System.nanoTime();
        // 预占令牌
        long at = reserve(now);
        long waitTime = Math.max(at - now, 0);
        // 按照条件等待
        if (waitTime > 0) {
            try {
                TimeUnit.NANOSECONDS.sleep(waitTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 预占令牌，返回能够获取令牌的时间
     * @param now
     * @return
     */
    private synchronized long reserve(long now) {
        resync(now);
        // 能够获取令牌的时间
        long at = next;
        // 令牌桶中能提供的令牌
        long fb = Math.min(1, storedPermits);
        // 令牌净需求：首先减掉令牌桶中的令牌
        long nr = 1 - fb;
        // 重新计算下一令牌产生时间
        next = next + nr * INTERVAL;
        // 重新计算令牌桶中的令牌
        storedPermits -= fb;
        return at;
    }

    /**
     * 请求时间在下一令牌产生时间之后,则
     *  1、重新计算令牌桶中的令牌数
     *  2、将下一个令牌发放时间重置为当前时间
     */
    private void resync(long now) {
        if (now > next) {
            // 新产生的令牌数
            long newPermits = (now - next) / INTERVAL;
            // 新令牌增加到令牌桶
            storedPermits = Math.min(maxPermits, storedPermits + newPermits);
            // 将下一个令牌发放时间重置为当前时间
            next = now;
        }
    }

    public static void main(String[] args) {
        class Task implements Runnable {

            private long prevTime;

            public Task(long prevTime) {
                this.prevTime = prevTime;
            }

            @Override
            public void run() {
                long currentTime = System.nanoTime();
                System.out.println((currentTime - prevTime) / 1000_000);
                prevTime = currentTime;
            }

        }

        MySimpleLimiter limiter = new MySimpleLimiter();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        long prevTime = System.nanoTime();
        for (int i = 0; i < 20; i++) {
            limiter.acquire();
            executorService.execute(new Task(prevTime));
        }
        executorService.shutdown();
    }

}
