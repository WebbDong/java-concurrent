package com.concurrent.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 使用读写锁实现一个缓存
 * @param <K>
 * @param <V>
 */
public class Cache<K, V> {

    private final Map<K, V> map = new HashMap<>();

    private final ReadWriteLock rwl = new ReentrantReadWriteLock();

    /**
     * 读锁
     */
    private final Lock readLock = rwl.readLock();

    /**
     * 写锁
     */
    private final Lock writeLock = rwl.writeLock();

    public V get(K key) {
        try {
            readLock.lock();
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public V put(K key, V value) {
        try {
            writeLock.lock();
            return map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

}
