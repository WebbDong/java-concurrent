package com.concurrent.tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * CompletableFuture
 *      1、默认情况下 CompletableFuture 会使用公共的 ForkJoinPool 线程池，这个线程池默认创建的线程数是 CPU 的核数
 *      2、CompletableFuture.supplyAsync(Supplier supplier)和CompletableFuture.runAsync(Runnable runnable)之间的区别是
 *          Runnable 接口的 run() 方法没有返回值，而 Supplier 接口的 get() 方法是有返回值的。
 */
public class CompletableFutureExample {

    private static void sleep(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 烧水泡茶例子
     */
    private static void example1() {
        // 任务1：洗水壶->烧开水
        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + ", T1:洗水壶...");
            sleep(1);

            System.out.println(Thread.currentThread().getName() + ", T1:烧开水...");
            sleep(3);
        });

        // 任务2：洗茶壶->洗茶杯->拿茶叶
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + ", T2:洗茶壶...");
            sleep(1);

            System.out.println(Thread.currentThread().getName() + ", T2:洗茶杯...");
            sleep(2);

            System.out.println(Thread.currentThread().getName() + ", T2:拿茶叶...");
            sleep(1);
            return "龙井";
        });

        // 任务3：任务1和任务2完成后执行：泡茶
        // tf是cf2的返回值
        CompletableFuture<String> cf3 = cf1.thenCombine(cf2, (__, tf) -> {
            System.out.println(Thread.currentThread().getName() + ", T1:拿到茶叶:" + tf);
            System.out.println(Thread.currentThread().getName() + ", T1:泡茶...");
            return "上茶:" + tf;
        });

        final String res = cf3.join();
        System.out.println(Thread.currentThread().getName() + " " + res);
    }

    /**
     * 串行关系
     */
    private static void example2() {
        var threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.setThreadFactory(new ThreadFactory() {
            private int num;

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, String.format("my-thread-%d", num++));
            }
        });
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            return "Hello World";
        }, threadPoolExecutor)
                .thenApply(str -> str + " Jupiter")
                .thenApply(String::toUpperCase);
        System.out.println(cf1.join());
        threadPoolExecutor.shutdown();
        System.out.println("----------------------------------");

        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "Lamborghini")
                .thenApplyAsync(s -> s + " lp700")
                .thenApplyAsync(String::toUpperCase);
        System.out.println(cf2.join());
        System.out.println("----------------------------------");

        CompletableFuture<Void> cf3 = CompletableFuture.runAsync(() -> System.out.println("CompletableFuture.runAsync"))
                .thenRun(() -> System.out.println("twitter"))
                .thenAccept(__ -> System.out.println("youtube"));
        cf3.join();
        System.out.println("----------------------------------");

        CompletableFuture<String> cf4 = CompletableFuture.supplyAsync(() -> "solar")
                // thenCompose会新创建出一个子流程
                .thenCompose(str -> CompletableFuture.supplyAsync(() -> str + " system")
                                        .thenApply(str2 -> str2 + " sun"));
        System.out.println(cf4.join());
    }

    /**
     * AND 汇聚关系
     *      所有依赖的任务都完成后才开始执行当前任务
     */
    private static void example3() {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "Hello World");
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "solar system");
        CompletableFuture<String> cf3 = cf1.thenCombine(cf2, (param1, param2) -> param1 + "--" + param2)
                .thenCombine(CompletableFuture.supplyAsync(() -> "--test"), (param1, param2) -> param1 + param2);
        System.out.println(cf3.join());
        System.out.println("----------------------------------");

        CompletableFuture<Void> cf4 = CompletableFuture.runAsync(() -> System.out.print("Ferrari"));
        CompletableFuture<Void> cf5 = CompletableFuture.runAsync(() -> System.out.print(" 599"));
        cf4.thenAcceptBoth(cf5, (__, ___) -> System.out.println(" GTO")).join();
        System.out.println("----------------------------------");

        CompletableFuture<Void> cf6 = CompletableFuture.runAsync(() -> System.out.print("Ferrari"));
        CompletableFuture<Void> cf7 = CompletableFuture.runAsync(() -> System.out.print(" F12"));
        cf6.runAfterBoth(cf7, () -> System.out.println(" Berlinetta")).join();
    }

    /**
     * OR 汇聚关系
     *      依赖的任务只要有一个完成就可以执行当前任务.
     */
    private static void example4() {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            sleep(2);
            System.out.println("cf1");
            return "Ferrari";
        });
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            sleep(5);
            System.out.println("cf2");
            return " F12";
        });
        System.out.println(cf1.applyToEither(cf2, (p1) -> p1 + " TDF").join());
        System.out.println("----------------------------------");

        CompletableFuture<Void> cf3 = CompletableFuture.runAsync(() -> {
            sleep(2);
            System.out.print("Ferrari");
        });
        CompletableFuture<Void> cf4 = CompletableFuture.runAsync(() -> {
            sleep(5);
            System.out.print(" 812");
        });
        cf3.acceptEither(cf4, __ -> System.out.println(" GTS")).join();
        System.out.println("----------------------------------");

        CompletableFuture<Void> cf5 = CompletableFuture.runAsync(() -> {
            sleep(2);
            System.out.print("Ferrari");
        });
        CompletableFuture<Void> cf6 = CompletableFuture.runAsync(() -> {
            sleep(6);
            System.out.print(" SF90");
        });
        cf5.runAfterEither(cf6, () -> System.out.println(" Stradale")).join();
    }

    /**
     * 异常处理
     *      CompletionStage exceptionally(fn); 非常类似于 try{}catch{} 中的 catch{}
     *      CompletionStage<R> whenComplete(consumer); 类似于 try{}finally{} 中的 finally{} 不支持返回结果
     *      CompletionStage<R> whenCompleteAsync(consumer); 异步whenComplete
     *      CompletionStage<R> handle(fn); 类似于 try{}finally{} 中的 finally{} 支持返回结果，handle的返回值优先级大于exceptionally
     *      CompletionStage<R> handleAsync(fn); 异步handle
     */
    private static void example5() {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> 7 / 0)
                .thenApply(n -> n * 10)
                .exceptionally(e -> {
                    System.out.println(e);
                    return 5;
                })
                .whenComplete((p1, p2) -> {
                    System.out.println("whenComplete：p1 = " + p1 + ", p2 = " + p2);
                })
                .handle((__, ___) -> {
                    System.out.println("handle");
                    return 10;
                });
        System.out.println(cf1.join());
        System.out.println("----------------------------------");

        Integer res = CompletableFuture.runAsync(() -> System.out.println(7 / 0))
                .handle((p1, p2) -> {
                    System.out.println("handle：p1 = " + p1 + ", p2 = " + p2);
                    return 500;
                })
                .join();
        System.out.println(res);
    }

    public static void main(String[] args) {
//        example1();
//        example2();
//        example3();
//        example4();
        example5();
    }

}
