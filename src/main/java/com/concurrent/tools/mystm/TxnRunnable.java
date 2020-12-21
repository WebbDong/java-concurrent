package com.concurrent.tools.mystm;

@FunctionalInterface
public interface TxnRunnable {

    void run(Txn txn);

}
