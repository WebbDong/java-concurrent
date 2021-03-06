package com.concurrent.tools;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用 Lock & Condition 实现阻塞队列
 */
public class MyBlockedQueue<T> {

    private final Lock lock = new ReentrantLock();

    /**
     * 条件变量：队列不满
     */
    private final Condition notFull = lock.newCondition();

    /**
     * 条件变量：队列不空
     */
    private final Condition notEmpty = lock.newCondition();

    private Object[] dataArr;

    private int cap;

    private int size;

    private int putIndex;

    private int takeIndex;

    public MyBlockedQueue() {
        this(10);
    }

    public MyBlockedQueue(int cap) {
        dataArr = new Object[cap];
        this.cap = cap;
    }

    /**
     * 入队
     * @param data
     */
    public void enq(T data) throws InterruptedException {
        try {
            lock.lock();
            // 队列已满
            while (size == cap) {
                // 当队列已满，阻塞入队操作
                notFull.await();
            }
            dataArr[putIndex++] = data;
            if (putIndex == dataArr.length) {
                putIndex = 0;
            }
            size++;
            // 唤醒出队操作
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 出队
     * @return
     */
    public T deq() throws InterruptedException {
        try {
            lock.lock();
            while (size == 0) {
                // 当队列为空时，阻塞出队操作
                notEmpty.await();
            }
            Object data = dataArr[takeIndex++];
            if (takeIndex == dataArr.length) {
                takeIndex = 0;
            }
            size--;
            // 唤醒入队操作
            notFull.signalAll();
            return (T) data;
        } finally {
            lock.unlock();
        }
    }

    public int getSize() {
        try {
            lock.lock();
            return size;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        MyBlockedQueue<Integer> queue = new MyBlockedQueue<>();
        new Thread(() -> {
            int num = 0;
            while (true) {
                try {
                    queue.enq(++num);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    System.out.println(Thread.currentThread().getName() + ": num = " + queue.deq());
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
