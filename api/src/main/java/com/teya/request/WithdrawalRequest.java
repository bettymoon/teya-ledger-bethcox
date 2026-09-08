package com.teya.request;

import java.util.UUID;

public class WithdrawalRequest {

    private UUID accountId;
    private Long withdrawalAmount;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public Long getWithdrawalAmount() {
        return withdrawalAmount;
    }

    public void setWithdrawalAmount(long withdrawalAmount) {
        this.withdrawalAmount = withdrawalAmount;
    }
}
