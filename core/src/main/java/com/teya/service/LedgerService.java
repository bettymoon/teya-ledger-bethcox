package com.teya.service;

import com.teya.dao.Account;
import com.teya.dao.Transaction;
import com.teya.dao.enums.TransactionType;

import java.util.*;

public class LedgerService {

    private final Map<UUID, Account> accounts = new HashMap<>();

    public Account createAccount(String accountName) {
        Account account = new Account(accountName);
        accounts.put(account.getAccountId(), account);
        return account;
    }

    public Account getAccount(UUID accountId) {
        return accounts.get(accountId);
    }

    public long deposit(UUID accountId, long amount) {
        Account account = getAccount(accountId);
        account.getAccountTransactions().add(new Transaction(TransactionType.DEPOSIT, amount));
        account.setBalance(account.getBalance() + amount);
        return account.getBalance();
    }

    public long withdraw(UUID accountId, long amount) {
        Account account = getAccount(accountId);
        account.getAccountTransactions().add(new Transaction(TransactionType.WITHDRAWAL, amount));
        account.setBalance(account.getBalance() - amount);
        return account.getBalance();
    }

    //because we are returning a long and it cannot be nullified, I have chosen to return -1L as negative balances
    // are not deemed allowed
    public long getBalance(UUID accountId) {
        Account account = getAccount(accountId);
        return account == null ? -1L : account.getBalance();
    }

    public List<Transaction> getAccountTransactions(UUID accountId) {
        Account account = getAccount(accountId);
        return account == null ? null : account.getAccountTransactions();
    }
}
