package com.concurrent.basic;

/**
 * join
 */
public class ThreadJoin {

    private static class Thread1 extends Thread {

        public Thread1(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println("Thread1.run");
            synchronized (this) {
                for (int i = 0; i < 100; i++) {
                    System.out.println(this.getName() + i);
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread1 t1 = new Thread1("Thread1 --- ");
        t1.start();

        synchronized (t1) {
            for (int i = 0; i < 100; i++) {
                if (i == 20) {
                    try {
                        // 谁先抢到锁谁先执行，另一个线程就被阻塞，当 main 打印到19后，join 方法让出了 CPU 执行时间片，
                        // t1 执行完后，main 会继续执行，main 执行完毕后，结束 JVM 进程。
                        // join() 内部调用的就是 wait(0)，当 t1 线程执行完毕后，JVM 会自动调用 notifyAll
//                        t1.join();

                        // 如果锁对象是线程对象，那么效果等于 t1.join()，当 t1 执行完毕后，JVM 会自动调用
                        // notifyAll 来唤醒其他正在等待的线程。
                        t1.wait(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName() + " -- " + i);
            }
        }
    }

}
