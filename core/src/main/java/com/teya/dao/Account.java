package com.teya.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {

    private final UUID accountId;
    private final String accountName;
    private long balance;
    private final List<Transaction> accountTransactions = new ArrayList<>();

    public Account(String accountName) {
        this.accountId = UUID.randomUUID();
        this.accountName = accountName;
        this.balance = 0;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public List<Transaction> getAccountTransactions() {
        return accountTransactions;
    }
}
