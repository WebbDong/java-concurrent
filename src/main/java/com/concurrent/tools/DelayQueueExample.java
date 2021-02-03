package com.concurrent.tools;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueue: 延迟队列
 *      DelayQueue 是 BlockingQueue 的一种，是线程安全的。插入Queue中的数据可以按照自定义的delay时间进行排序。
 *      只有delay时间小于0的元素才能够被取出。
 */
public class DelayQueueExample {

    @Data
    @NoArgsConstructor
    private static class DelayItem implements Delayed {

        /**
         * 延迟执行的时间，单位毫秒
         */
        private long availableTime;

        private String data;

        public DelayItem(long delayTime, String data) {
            // 当前系统时间加上延迟的时间
            this.availableTime = System.currentTimeMillis() + delayTime;
            this.data = data;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = availableTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            DelayItem other = (DelayItem) o;
            return (int) (this.availableTime - other.getAvailableTime());
        }

    }

    public static void main(String[] args) throws InterruptedException {
        DelayQueue<DelayItem> delayQueue = new DelayQueue<>();
        // 延迟5秒
        delayQueue.put(new DelayItem(5000, "DelayItem1"));
        // 延迟10秒
        delayQueue.put(new DelayItem(10000, "DelayItem2"));

        for (int i = 0, size = delayQueue.size(); i < size; i++) {
            DelayItem item = delayQueue.take();
            System.out.println(item.getData());
        }
    }

}
