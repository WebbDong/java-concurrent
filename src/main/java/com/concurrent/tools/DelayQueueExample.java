package com.concurrent.tools;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueue: 延迟队列
 */
public class DelayQueueExample {

    private static class DelayItem implements Delayed {

        private long time;

        @Override
        public long getDelay(TimeUnit unit) {
            return 0;
        }

        @Override
        public int compareTo(Delayed o) {

            return 0;
        }

    }

    public static void main(String[] args) {
        DelayQueue<DelayItem> delayQueue = new DelayQueue<>();
    }

}
