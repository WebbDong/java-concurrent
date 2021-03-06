package com.concurrent.tools.atomic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger
         getAndIncrement() //原子化i++
         getAndDecrement() //原子化的i--
         incrementAndGet() //原子化的++i
         decrementAndGet() //原子化的--i
         //当前值+=delta，返回+=前的值
         getAndAdd(delta)
         //当前值+=delta，返回+=后的值
         addAndGet(delta)
         //CAS操作，返回是否成功
         compareAndSet(expect, update)
         //以下四个方法
         //新值可以通过传入func函数来计算
         getAndUpdate(func)
         updateAndGet(func)
         getAndAccumulate(x,func)
         accumulateAndGet(x,func)
 */
public class AtomicIntegerExample {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(10);
        var threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());
        final Runnable runnable = () -> {
            for (int i = 0; i < 1000; i++) {
                atomicInteger.incrementAndGet();
            }
            cdl.countDown();
        };
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(runnable);
        }

        cdl.await();
        threadPoolExecutor.shutdown();
        System.out.println(atomicInteger.get());
    }

}
