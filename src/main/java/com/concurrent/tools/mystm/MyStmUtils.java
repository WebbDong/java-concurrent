package com.concurrent.tools.mystm;

/**
 * STM
 */
public final class MyStmUtils {

    /**
     * 提交数据需要用到的全局锁
     */
    public static final Object COMMIT_LOCK = new Object();

    private MyStmUtils() {}

    /**
     * 原子化提交方法
     * @param runnable
     */
    public static void atomic(TxnRunnable runnable) {
        // 是否提交成功
        boolean committed = false;

        // 如果没有提交成功，则一直重试
        while (!committed) {
            // 创建新的事务
            StmTxn txn = new StmTxn();
            // 执行业务逻辑
            runnable.run(txn);
            // 提交事务
            committed = txn.commit();
        }
    }

}
