package com.teya.dao;

import com.teya.dao.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private final UUID transactionId = UUID.randomUUID();
    private final TransactionType transactionType;
    private final long transactionAmount;
    private final LocalDateTime transactionTimestamp = LocalDateTime.now();

    public Transaction(TransactionType transactionType, long transactionAmount) {
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public long getTransactionAmount() {
        return transactionAmount;
    }

    public LocalDateTime getTransactionTimestamp() {
        return transactionTimestamp;
    }
}
