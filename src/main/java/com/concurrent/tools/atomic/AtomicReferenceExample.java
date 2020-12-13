package com.concurrent.tools.atomic;

import com.concurrent.model.Account;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceExample {

    private static AtomicReference<Account> atomicReference = new AtomicReference(new Account(1L, 9000));

    public static void main(String[] args) {
        System.out.println(atomicReference.get().getBalance());
    }

}
