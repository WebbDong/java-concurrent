package com.concurrent.designpattern;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * Guarded Suspension模式实现
 * @param <T>
 */
class GuardedSuspensionObject<T> {

    /**
     * 受保护的对象
     */
    private T obj;

    private final Lock lock = new ReentrantLock();

    private final Condition done = lock.newCondition();

    private final int TIME_OUT = 2;

    private final static Map<Object, GuardedSuspensionObject> gos = new ConcurrentHashMap<>();

    /**
     * 创建GuardedObject
     * @param key
     * @param <K>
     * @return
     */
    public static <K> GuardedSuspensionObject create(K key) {
        GuardedSuspensionObject go = new GuardedSuspensionObject();
        gos.put(key, go);
        return go;
    }

    /**
     * 触发事件
     * @param key
     * @param obj
     * @param <K>
     * @param <T>
     */
    public static <K, T> void fireEvent(K key, T obj) {
        final GuardedSuspensionObject<T> go = gos.remove(key);
        if (go != null) {
            go.onChange(obj);
        }
    }

    /**
     * 获取受保护对象
     * @param p
     * @return
     */
    public T get(Predicate<T> p) {
        try {
            lock.lock();
            // MESA管程推荐写法
            while (!p.test(obj)) {
                // 等待2秒并释放锁
                done.await(TIME_OUT, TimeUnit.SECONDS);
            }
            return obj;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 事件通知
     * @param obj
     */
    private void onChange(T obj) {
        try {
            lock.lock();
            this.obj = obj;
            done.signalAll();
        } finally {
            lock.unlock();
        }
    }

}

/**
 * Guarded Suspension模式
 *      常用在异步转同步的场景中，是一个经典的管程实现，本质上是一种等待唤醒机制的实现。
 */
public class GuardedSuspensionPattern {

    private static final int ID = 10;

    public static void main(String[] args) {
        GuardedSuspensionObject<Integer> go = GuardedSuspensionObject.create(ID);
        asyncFunc();
        // 异步转同步
        final Integer resSum = go.get(obj -> obj != null);
        System.out.println("sum = " + resSum);
    }

    /**
     * 异步方法
     */
    private static void asyncFunc() {
        new Thread(() -> {
            try {
                int sum = 0;
                for (int i = 0; i < 500; i++) {
                    sum++;
                }
                TimeUnit.SECONDS.sleep(2);
                // 触发事件，唤醒等待的线程
                GuardedSuspensionObject.fireEvent(ID, sum);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
