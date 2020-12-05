package com.concurrent;

/**
 * interrupt中断
 *
 * stop() 方法会真的杀死线程，不给线程喘息的机会，如果线程持有 ReentrantLock 锁，被 stop() 的线程并不会自动调用 ReentrantLock
 * 的 unlock() 去释放锁，那其他线程就再也没机会获得 ReentrantLock 锁，这实在是太危险了。所以该方法就不建议使用了，类似的方法还有
 * suspend() 和 resume() 方法，这两个方法同样也都不建议使用了。
 *
 * 而 interrupt() 方法就温柔多了，interrupt() 方法仅仅是通知线程，线程有机会执行一些后续操作，同时也可以无视这个通知。
 * 被 interrupt 的线程，是怎么收到通知的呢？一种是异常，另一种是主动检测。
 *
 * 当线程 A 处于 WAITING、TIMED_WAITING 状态时，如果其他线程调用线程 A 的 interrupt() 方法，会使线程 A 返回到 RUNNABLE 状态，
 * 同时线程 A 的代码会触发 InterruptedException 异常。上面我们提到转换到 WAITING、TIMED_WAITING 状态的触发条件，
 * 都是调用了类似 wait()、join()、sleep() 这样的方法，我们看这些方法的签名，发现都会 throws InterruptedException 这个异常。
 * 这个异常的触发条件就是：其他线程调用了该线程的 interrupt() 方法。
 *
 * 当线程 A 处于 RUNNABLE 状态时，并且阻塞在 java.nio.channels.InterruptibleChannel 上时，如果其他线程调用线程 A
 * 的 interrupt() 方法，线程 A 会触发 java.nio.channels.ClosedByInterruptException 这个异常；而阻塞在
 * java.nio.channels.Selector 上时，如果其他线程调用线程 A 的 interrupt() 方法，线程 A 的 java.nio.channels.Selector 会立即返回。
 *
 * 上面这两种情况属于被中断的线程通过异常的方式获得了通知。还有一种是主动检测，如果线程处于 RUNNABLE 状态，并且没有阻塞在某个
 * I/O 操作上，例如中断计算圆周率的线程 A，这时就得依赖线程 A 主动检测中断状态了。如果其他线程调用线程 A 的 interrupt() 方法，
 * 那么线程 A 可以通过 isInterrupted() 方法，检测是不是自己被中断了。
 */
public class ThreadInterrupt {

    public static void main(String[] args) throws Exception {
//        test1();
        test2();
    }

    /**
     * 如果线程处于 RUNNABLE 状态，并且没有阻塞在某个 I/O 操作上，例如中断计算圆周率的线程 A，这时就得依赖线程 A 主动检测中断状态了。
     * 如果其他线程调用线程 A 的 interrupt() 方法，那么线程 A 可以通过 isInterrupted() 方法，检测是不是自己被中断了。
     * @throws InterruptedException
     */
    private static void test2() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
        });
        thread1.start();
        Thread.sleep(1000);
        thread1.interrupt();
        while (true) {
            System.out.println("thread1.getState() = " + thread1.getState());
            Thread.sleep(500);
        }
    }

    /**
     * 当线程 A 处于 WAITING、TIMED_WAITING 状态时，如果其他线程调用线程 A 的 interrupt() 方法，会使线程 A 返回到 RUNNABLE 状态，
     * 同时线程 A 的代码会触发 InterruptedException 异常。
     * @throws InterruptedException
     */
    private static void test1() throws InterruptedException {
        final Object lockObj = new Object();
        Runnable runnable = () -> {
            synchronized (lockObj) {
                try {
                    lockObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (long i = 0, len = Integer.MAX_VALUE; i < len; i++)
                    ;
            }
        };
        Thread thread1 = new Thread(runnable);
        thread1.start();
        thread1.interrupt();
        while (true) {
            System.out.println("thread1.getState() = " + thread1.getState());
            Thread.sleep(200);
        }
    }

}
