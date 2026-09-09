package com.teya.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.teya.request.DepositRequest;
import com.teya.request.WithdrawalRequest;
import com.teya.service.LedgerService;
import com.teya.dao.Transaction;
import com.teya.dao.Account;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    private final LedgerService service;

    public LedgerController(LedgerService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Account createAccount(@RequestBody Map<String, String> createRequest) {
        if(createRequest.isEmpty() || createRequest.get("accountName") == null || createRequest.get("accountName").isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Name must be provided.");
        return service.createAccount(createRequest.get("accountName"));
    }

    @PostMapping("/deposit")
    public Map<String, Object> deposit(@RequestBody DepositRequest requestBody) {

        if(requestBody.getAccountId() == null || requestBody.getDepositAmount() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deposit Request must include both Account ID and Deposit Amount");

        if(service.getAccount(requestBody.getAccountId()) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");

        if(requestBody.getDepositAmount() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount to be deposited must be over 0.");

        long balance = service.deposit(requestBody.getAccountId(), requestBody.getDepositAmount());
        return Map.of("balance", balance);
    }

    @PostMapping("/withdraw")
    public Map<String, Object> withdraw(@RequestBody WithdrawalRequest requestBody) {

        if(requestBody.getAccountId() == null || requestBody.getWithdrawalAmount() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Withdrawal Request must include both Account ID and Deposit Amount");

        if(service.getAccount(requestBody.getAccountId()) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");

        if(requestBody.getWithdrawalAmount() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount to be withdrawn must be over 0.");

        long balance = service.getBalance(requestBody.getAccountId());

        if(requestBody.getWithdrawalAmount() > balance)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount to be withdrawn exceeds balance. Please withdraw a smaller amount.");

        return Map.of("balance", service.withdraw(requestBody.getAccountId(), requestBody.getWithdrawalAmount()));
    }

    @GetMapping("/balance")
    public Map<String, Object> getBalance(@RequestHeader String accountId) {
        if(accountId == null || accountId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Id must be provided.");

        if(service.getAccount(UUID.fromString(accountId)) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");

        long balance = service.getBalance(UUID.fromString(accountId));

        if(balance == -1L)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Balance is not available.");

        return Map.of("balance", balance);
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestHeader String accountId) {
        if(accountId == null || accountId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Id must be provided.");
        if(service.getAccount(UUID.fromString(accountId)) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");

        List<Transaction> transactionHistory = service.getAccountTransactions(UUID.fromString(accountId));

        if(transactionHistory == null || transactionHistory.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No transactions found.");

        return transactionHistory;

    }
}
