package com.concurrent.model;

import com.concurrent.tools.mystm.MyStmUtils;
import com.concurrent.tools.mystm.TxnRef;
import lombok.Data;

/**
 * 使用自己的STM实现原子的转账操作
 */
@Data
public class AccountMyStm {

    private TxnRef<Integer> balance;

    public AccountMyStm(int balance) {
        this.balance = new TxnRef<>(balance);
    }

    public void transfer(AccountMyStm target, int amt) {
        MyStmUtils.atomic((txn) -> {
            Integer from = balance.getValue(txn);
            balance.setValue(from - amt, txn);
            Integer to = target.balance.getValue(txn);
            target.balance.setValue(to + amt, txn);
        });
    }

}
