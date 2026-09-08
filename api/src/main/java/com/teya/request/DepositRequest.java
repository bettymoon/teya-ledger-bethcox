package com.teya.request;

import java.util.UUID;

public class DepositRequest {

    private UUID accountID;
    private Long depositAmount;

    public UUID getAccountId() {
        return accountID;
    }

    public void setAccountId(UUID accountID) {
        this.accountID = accountID;
    }

    public Long getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(long depositAmount) {
        this.depositAmount = depositAmount;
    }
}
