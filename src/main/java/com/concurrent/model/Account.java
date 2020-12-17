package com.concurrent.model;

import lombok.Data;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@Data
public class Account {

    private Long id;

    private int balance;

    private Allocator allocator = Allocator.getInstance();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * 锁管理员
     */
    private static class Allocator {

        private List<Object> usingLockList = new ArrayList<>();

        private static class Inner {
            public static Allocator INSTANCE = new Allocator();
        }

        private Allocator() {}

        /**
         * 申请所有资源
         * @param from
         * @param to
         * @return
         */
        public synchronized boolean apply(Object from, Object to) {
            if (usingLockList.contains(from)
                    || usingLockList.contains(to)) {
                return false;
            } else {
                usingLockList.add(from);
                usingLockList.add(to);
                return true;
            }
        }

        /**
         * 归还资源
         * @param from
         * @param to
         */
        public synchronized void free(Object from, Object to) {
            usingLockList.remove(from);
            usingLockList.remove(to);
        }

        @SneakyThrows
        public synchronized void applyWithWait(Object from, Object to) {
            // 经典写法，notify() 和 notifyAll() 只能保证在通知时间点，条件是满足的。而被通知线程的执行时间点和通知的时间点基本上不会重合，
            // 所以当线程执行的时候，很可能条件已经不满足了。需要使用循环来再次判断条件
            while (usingLockList.contains(from)
                    || usingLockList.contains(to)) {
                this.wait();
            }
            usingLockList.add(from);
            usingLockList.add(to);
        }

        public synchronized void freeWithNotifyAll(Object from, Object to) {
            usingLockList.remove(from);
            usingLockList.remove(to);
            // 使用notifyAll，不要使用notify
            this.notifyAll();
        }

        public static Allocator getInstance() {
            return Inner.INSTANCE;
        }

    }

    public Account(int balance) {
        this.balance = balance;
    }

    public Account(long id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    // 线程不安全
    public void transferThreadUnsafe(Account target, int amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            target.balance += amount;
        }
    }

    // 不能将synchronized加在方法上，对象不同情况时时，锁并不是同一把，无法锁定共享资源
    /*
    public void transfer(Account target, int amount) {
        // 使用类对象做锁，性能太差
        synchronized (Account.class) {
            if (this.balance >= amount) {
                this.balance -= amount;
                target.balance += amount;
            }
        }
    }
     */

    public void transferDeadLock(Account target, int amount) {
        // 假设有2个account对象a1和a2，有2个线程，第1个线程里调用a1.transferDeadLock(a2, 100)，第2个线程里调用a2.transferDeadLock(a1, 100)
        // 就会存在死锁情况。
        synchronized (this) {
            synchronized (target) {
                if (this.balance >= amount) {
                    this.balance -= amount;
                    target.balance += amount;
                }
            }
        }
    }

    // 破坏占用且等待条件，防止死锁
    // 使用锁管理员，只允许线程一次性获取所有资源的锁，不允许线程获取部分的锁
    /*
    public void transfer(Account target, int amount) {
        // 一次性申请转出账户和转入账户，直到成功为止。
        while (!allocator.apply(this, target))
            ;
        try {
            // 锁定转出账户
            synchronized (this) {
                // 锁定转入账户
                synchronized (target) {
                    if (this.balance >= amount) {
                        this.balance -= amount;
                        target.balance += amount;
                    }
                }
            }
        } finally {
            // 释放锁
            allocator.free(this, target);
        }
    }
     */

    // 破坏循环等待条件，防止死锁
    // 顺序化锁，进而破坏循环等待条件
    /*
    public void transfer(Account target, int amount) {
        Account small = this;
        Account big = target;

        if (this.id > target.id) {
            small = target;
            big = this;
        }
        // 锁定id小的账户
        synchronized (small) {
            // 锁定id大的账户
            synchronized (big) {
                if (this.balance >= amount) {
                    this.balance -= amount;
                    target.balance += amount;
                }
            }
        }
    }
     */

    // 破坏占用且等待条件，防止死锁
    // 使用锁管理员，只允许线程一次性获取所有资源的锁，不允许线程获取部分的锁
    // 使用等待“等待-通知”机制优化循环等待
    @SneakyThrows
    public void transfer(Account target, int amount) {
        // 一次性申请转出账户和转入账户，直到成功为止。
        allocator.applyWithWait(this, target);
        try {
            // 锁定转出账户
            synchronized (this) {
                // 锁定转入账户
                synchronized (target) {
                    if (this.balance >= amount) {
                        this.balance -= amount;
                        target.balance += amount;
                    }
                }
            }
        } finally {
            // 释放锁
            allocator.freeWithNotifyAll(this, target);
        }
    }

}
