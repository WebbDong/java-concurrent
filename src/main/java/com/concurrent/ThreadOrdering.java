package com.concurrent;

/**
 * 并发有序性
 *      并不能保证代码执行到“关注点”处，var变量的值一定是3。因为在startSystem方法中的两个赋值语句并不存在依赖关系，
 *      所以在编译器进行代码编译时可能进行指令重排。所以真实的执行顺序可能是下面这样的。
 *          started = true;
 *          value = 2;
 *      也就是先执行started = true;执行完这个语句后，线程立马执行checkStartes方法，此时value值还是1，那么最后在关注点处的var值就是2，而不是我们想象中的3。
 */
public class ThreadOrdering {

    private static int value = 1;

    private static boolean started = false;

    public static void startSystem() {
        value = 2;
        started = true;
    }

    public static void checkStartes() {
        if (started) {
            // 关注点
            int var = value + 1;
            System.out.println("system is running var = " + var);
        } else {
            System.out.println("system is not running");
        }
    }

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            while (true) {
                startSystem();
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            while (true) {
                checkStartes();
            }
        });
        t2.start();
    }

}
