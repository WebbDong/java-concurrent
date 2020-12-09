package com.concurrent.tools;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 使用Semaphore实现对象池
 *      需要使用Vector保证线程安全，因为Semaphore允许多个线程进入，所以remove和add存在并发安全问题
 */
public class ObjPool<T, R> {

    private final List<T> pool;

    // 用信号量实现限流器
    private final Semaphore semaphore;

    public ObjPool(int size, T obj) {
        semaphore = new Semaphore(size);
        pool = new Vector<>(size);
        for (int i = 0; i < size; i++) {
            pool.add(obj);
        }
    }

    public R exec(Function<T, R> func) throws InterruptedException {
        T t = null;
        try {
            // 当信号量计数器小于0时，其他线程会在此阻塞
            semaphore.acquire();
            t = pool.remove(0);
            return func.apply(t);
        } finally {
            // 把使用完毕的对象重新放入池中
            pool.add(t);
            // 释放信号量，计数器加1
            semaphore.release();
        }
    }

    public static void main(String[] args) throws Exception {
        ObjPool<Long, String> objPool = new ObjPool<>(10, 2L);
        Runnable runnable = () -> {
            try {
                for (int i = 0; i < 500; i++) {
                    objPool.exec(t -> {
                        System.out.println(t);
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return t.toString();
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        var threadPoolExecutor = new ThreadPoolExecutor(
                50,
                200,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i < 200; i++) {
            threadPoolExecutor.execute(runnable);
        }
        threadPoolExecutor.execute(runnable);
        threadPoolExecutor.shutdown();
    }

}
