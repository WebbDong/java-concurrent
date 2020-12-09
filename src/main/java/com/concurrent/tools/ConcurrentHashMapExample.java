package com.concurrent.tools;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrentHashMapExample {

    private static int key = 0;

    private static int num = 0;

    public static void main(String[] args) {
        Map<Object, Object> map1 = new ConcurrentHashMap<>();
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());
        final Runnable runnable = () -> {
            map1.put(key, num);
            key++;
            num++;
        };
        for (var i = 0; i < 10; i++) {
            threadPoolExecutor.execute(runnable);
        }
    }

}
