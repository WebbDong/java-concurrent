package com.concurrent.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Phaser
 *      Phaser 是 java7 引入的新的并发工具。可以将其看成一个一个的阶段。
 *      适用场景: 一种任务可以分为多个阶段，并且希望多线程去处理每个阶段的任务，同时必须一个阶段一个阶段的执行。在 java7 之前，可以使用多个
 *          CyclicBarrier 来实现，每个阶段使用一个 CyclicBarrier 类实现等待和处理。但 Phaser 可以更加灵活的实现这类需求。
 *
 *      Phaser 同样也是通过计数器来控制。在 Phaser 中计数器叫做 parties。
 *
 * Phaser api:
 *      Phaser() 构造方法:
 *      Phaser(int parties) 构造方法:
 *      Phaser(Phaser parent) 构造方法:
 *      Phaser(Phaser parent, int parties) 构造方法:
 *
 *      register():
 *      arriveAndAwaitAdvance():
 *      arrive():
 *      arriveAndDeregister():
 *
 * 示例场景:
 *      10个学生一起参加考试，一共有三场考试，要求所有学生到齐才能开始考试，全部同学都完成第一场考试后，学生才能进行第二场考试，
 *      全部学生完成第二场考试后，才能进行第三场考试，所有学生都完成第三场考试后，考试才结束。
 */
public class PhaserExample {

    private static class ExamPhaser extends Phaser {

        public ExamPhaser() {
        }

        public ExamPhaser(int parties) {
            super(parties);
        }

        public ExamPhaser(Phaser parent) {
            super(parent);
        }

        public ExamPhaser(Phaser parent, int parties) {
            super(parent, parties);
        }

        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            switch (phase) {
                case 0:
                    System.out.println("所有学生已到考场...");
                    System.out.println("---------------------------------");
                    return false;
                case 1:
                    System.out.println("第一场考试所有学生已完成...");
                    System.out.println("---------------------------------");
                    return false;
                case 2:
                    System.out.println("第二场考试所有学生已完成...");
                    System.out.println("---------------------------------");
                    return false;
                case 3:
                    System.out.println("第三场考试所有学生已完成...");
                    System.out.println("---------------------------------");
                    return true;
                default:
                    return true;
            }
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ExamTask implements Runnable {

        private Phaser phaser;

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "到达考场");
            phaser.arriveAndAwaitAdvance();

            System.out.println(Thread.currentThread().getName() + "第一题完成...");
            phaser.arriveAndAwaitAdvance();

            System.out.println(Thread.currentThread().getName() + "第二题完成...");
            phaser.arriveAndAwaitAdvance();

            System.out.println(Thread.currentThread().getName() + "第三题完成...");
            phaser.arriveAndAwaitAdvance();
        }

    }

    private static class StudentThreadFactory implements ThreadFactory {

        private static int i;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, new StringBuilder("学生-").append(++i).toString());
        }

    }

    public static void main(String[] args) {
        final int STUDENT_COUNT = 10;
        final Phaser phaser = new ExamPhaser(STUDENT_COUNT);
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                STUDENT_COUNT,
                STUDENT_COUNT,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.AbortPolicy());

        executor.setThreadFactory(new StudentThreadFactory());
        IntStream.range(0, STUDENT_COUNT).forEach(i -> executor.execute(new ExamTask(phaser)));
        executor.shutdown();
    }

}
