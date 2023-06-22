package com.concurrent.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * Fork/Join
 *      Fork/Join 是一个并行计算的框架，主要就是用来支持分治任务模型的，这个计算框架里的 Fork 对应的是分治任务模型里的任务分解，
 *      Join 对应的是结果合并。Fork/Join 计算框架主要包含两部分，一部分是分治任务的线程池 ForkJoinPool，另一部分是分治任务 ForkJoinTask。
 *      ForkJoinPool 支持任务窃取机制，能够让所有线程的工作量基本均衡，不会出现有的线程很忙，而有的线程很闲的状况。
 *
 */
public class ForkJoinExample {

    /**
     * 使用Fork/Join实现Fibonacci数列
     */
    private static void example1() {
        // 递归任务
        class FibonacciTask extends RecursiveTask<Integer> {

            private final int n;

            public FibonacciTask(int n) {
                this.n = n;
            }

            @Override
            protected Integer compute() {
                if (n <= 1) {
                    return n;
                }
                FibonacciTask ft1 = new FibonacciTask(n - 1);
                // 创建子任务
                ft1.fork();
                FibonacciTask ft2 = new FibonacciTask(n - 2);
                // 等待子任务结果，并合并结果
                return ft2.compute() + ft1.join();

/*                FibonacciTask ft1 = new FibonacciTask(n - 1);
                FibonacciTask ft2 = new FibonacciTask(n - 2);
                // 把拆分之后的两个新任务提交给任务池
                invokeAll(ft1, ft2);
                return ft1.join() + ft2.join();*/
            }

        }

        // 创建分治任务线程池
        ForkJoinPool fjp = new ForkJoinPool(4);
        // 创建分治任务
        FibonacciTask ft = new FibonacciTask(30);
        // 启动分治任务
        final Integer res = fjp.invoke(ft);
        // 832040
        System.out.println(res);
    }

    /**
     * 使用Fork/Join实现数组元素之和
     */
    private static void example2() {
        // 数组长度
        final int ARRAY_LENGTH = 10000;
        int[] arr = new int[ARRAY_LENGTH];
        int sum = 0;
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            arr[i] = i;
            sum += i;
        }
        System.out.println(sum);

        class SumTask extends RecursiveTask<Integer> {

            /**
             * 阈值，每1000个元素为一段数据
             */
            private final int THRESHOLD = ARRAY_LENGTH / 10;

            private int[] arr;

            /**
             * 开始数组下标
             */
            private int startIndex;

            /**
             * 结束数组下标
             */
            private int endIndex;

            public SumTask(int[] arr, int startIndex, int endIndex) {
                this.arr = arr;
                this.startIndex = startIndex;
                this.endIndex = endIndex;
            }

            /**
             * 递归计算
             *      如果要操作的数据小于等于阈值，直接进行计算，否则递归拆分。
             * @return
             */
            @Override
            protected Integer compute() {
                if (endIndex - startIndex <= THRESHOLD) {
                    // 小于等于阈值，直接计算
                    int sum = 0;
                    for (int i = startIndex; i < endIndex; i++) {
                        sum += arr[i];
                    }
                    return sum;
                } else {
                    int middle = (startIndex + endIndex) / 2;
                    /*
                    // 数组前半部分
                    SumTask st1 = new SumTask(arr, startIndex, middle);
                    // 数组后半部分
                    SumTask st2 = new SumTask(arr, middle, endIndex);
                    // 把拆分之后的新任务再次提交给任务池
                    invokeAll(st1, st2);
                    return st1.join() + st2.join();
                     */

                    SumTask st1 = new SumTask(arr, startIndex, middle);
                    SumTask st2 = new SumTask(arr, middle, endIndex);
                    st1.fork();
                    return st2.compute() + st1.join();
                }
            }

        }

        ForkJoinPool fjp = new ForkJoinPool();
        SumTask sumTask = new SumTask(arr, 0, ARRAY_LENGTH);
        fjp.invoke(sumTask);
        System.out.println(sumTask.join());
    }

    /**
     * 使用Fork/Join实现单词数量统计
     */
    private static void example3() {
        class WordCountTask extends RecursiveTask<Map<String, Long>> {

            private String[] fc;

            private int start;

            private int end;

            public WordCountTask(String[] fc, int start, int end) {
                this.fc = fc;
                this.start = start;
                this.end = end;
            }

            @Override
            protected Map<String, Long> compute() {
                if (end - start == 1) {
                    return calc(fc[start]);
                } else {
                    int middle = (start + end) / 2;
                    /*
                    WordCountTask t1 = new WordCountTask(fc, start, middle);
                    t1.fork();
                    WordCountTask t2 = new WordCountTask(fc, middle, end);
                    return merge(t2.compute(), t1.join());
                     */

                    WordCountTask t1 = new WordCountTask(fc, start, middle);
                    WordCountTask t2 = new WordCountTask(fc, middle, end);
                    invokeAll(t1, t2);
                    return merge(t1.join(), t2.join());
                }
            }

            /**
             * 合并结果
             * @param r1
             * @param r2
             * @return
             */
            private Map<String, Long> merge(Map<String, Long> r1, Map<String, Long> r2) {
                Map<String, Long> result = new HashMap<>();
                result.putAll(r1);

                // 合并结果
                r2.forEach((k, v) -> {
                    Long num = result.get(k);
                    if (num == null) {
                        result.put(k, v);
                    } else {
                        result.put(k, num + v);
                    }
                });
                return result;
            }

            /**
             * 统计单词
             * @param line
             * @return
             */
            private Map<String, Long> calc(String line) {
                Map<String, Long> result = new HashMap<>();
                String[] words = line.split("\\s+");
                // 分割单词
                // 统计单词数量
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    Long v = result.get(word);
                    if (v == null) {
                        result.put(word, 1L);
                    } else {
                        result.put(word, v + 1);
                    }
                }
                return result;
            }

        }

        String[] fc = {"hello world", "hello me", "hello fork", "hello join", "fork join in world"};
        ForkJoinPool fjp = new ForkJoinPool(3);
        WordCountTask task = new WordCountTask(fc, 0, fc.length);
        Map<String, Long> result = fjp.invoke(task);
        System.out.println(result);
    }

    /**
     * 使用Fork/Join寻找打印某个目录的指定的文件类型
     */
    private static void example4() {
        class FindFilesTask extends RecursiveAction {

            private File rootPath;

            public FindFilesTask(String path) {
                rootPath = new File(path);
            }

            public FindFilesTask(File path) {
                rootPath = path;
            }

            @Override
            protected void compute() {
                File[] files = rootPath.listFiles();
                if (files == null || files.length == 0) {
                    return;
                }
                List<FindFilesTask> taskList = new ArrayList<>();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.isDirectory()) {
                        // 拆分任务
                        taskList.add(new FindFilesTask(file));
                    } else {
                        if (file.getName().endsWith(".mp4")) {
                            System.out.println(String.format("%s\\%s", file.getAbsolutePath(), file.getAbsoluteFile().getName()));
                        }
                    }
                }
                if (!taskList.isEmpty()) {
                    invokeAll(taskList);
                }
            }

        }

        ForkJoinPool fjp = new ForkJoinPool();
        FindFilesTask task = new FindFilesTask("F:\\download");
        fjp.execute(task);
        task.join();
    }

    public static void main(String[] args) {
//        example1();
//        example2();
//        example3();
        example4();
    }

}
