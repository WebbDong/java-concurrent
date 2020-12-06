package com.concurrent.basic;

/**
 * 活锁
 *      任务没有被阻塞，由于某些条件没有满足，导致一直重复尝试—失败—尝试—
 *      失败的过程，导致程序无法正常执行下去。活锁有可能自行解开
 *
 * 活锁例子
 *      消息重试。当某个消息处理失败的时候，一直重试，但重试由于某种原因，比如消息格式不对，导致解析失败，而它又被重试，无限循环
 */
public class LiveLock {

    public static void main(String[] args) {
    }

}
