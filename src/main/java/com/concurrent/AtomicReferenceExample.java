package com.concurrent;

import com.concurrent.model.Account;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceExample {

    private static AtomicReference<String> stringAtomicReference = new AtomicReference<>();

    private static AtomicReference<Account> accountAtomicReference = new AtomicReference<>();

    public static void main(String[] args) throws Exception {
    }

}
