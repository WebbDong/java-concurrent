package com.concurrent.model;

import lombok.Data;
import org.multiverse.api.StmUtils;
import org.multiverse.api.references.TxnLong;

/**
 * 使用Multiverse框架，STM
 */
@Data
public class AccountMultiverse {

    /**
     * 余额
     */
    private TxnLong balance;

    public AccountMultiverse(long balance) {
        this.balance = StmUtils.newTxnLong(balance);
    }

    /**
     * 转账
     * @param to
     * @param amt
     */
    public void transfer(AccountMultiverse to, int amt) {
        // 原子化操作
        StmUtils.atomic(() -> {
            if (this.balance.get() > amt) {
                this.balance.decrement(amt);
                to.balance.increment(amt);
            }
        });
    }

}
