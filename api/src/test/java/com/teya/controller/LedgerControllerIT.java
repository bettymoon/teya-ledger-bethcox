package com.teya.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teya.dao.enums.TransactionType;
import com.teya.request.DepositRequest;
import com.teya.request.WithdrawalRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.teya.service.LedgerService;
import com.teya.dao.Account;
import com.teya.dao.Transaction;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LedgerController.class)
public class LedgerControllerIT {

    @Autowired
    private MockMvc mock;

    @MockBean
    private LedgerService ledgerService;

    @Autowired
    private ObjectMapper objectMapper;

    //Postive Tests

    @Test
    void createNewAccount() throws Exception {
        when(ledgerService.createAccount(anyString())).thenReturn(new Account("John Doe"));

        Map<String, String> request = new HashMap<>();
        request.put("accountName", "John Doe");

        mock.perform(post("/api/ledger/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName").value("John Doe"));
    }

    @Test
    void depositAddsMoneyAndRespondsWithNewBalance() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(new Account("John Doe"));
        when(ledgerService.deposit(any(UUID.class), anyLong())).thenReturn(1000L);

        DepositRequest request = new DepositRequest();
        request.setAccountId(UUID.randomUUID());
        request.setDepositAmount(10);
        mock.perform(post("/api/ledger/deposit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void withdrawRemovesMoneyAndRespondsWithNewBalance() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(new Account("John Doe"));
        when(ledgerService.getBalance(any(UUID.class))).thenReturn(1010L);
        when(ledgerService.withdraw(any(UUID.class), anyLong())).thenReturn(1000L);

        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountId(UUID.randomUUID());
        request.setWithdrawalAmount(10);

        mock.perform(post("/api/ledger/withdraw")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void getBalanceRetrievesCurrentBalance() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(new Account("John Doe"));
        when(ledgerService.getBalance(any(UUID.class))).thenReturn(1000L);

        mock.perform(get("/api/ledger/balance").param("accountId", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void getTransactionHistoryRetrievesAListOfTransactions() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(new Account("John Doe"));
        when(ledgerService.getAccountTransactions(any(UUID.class))).thenReturn(List.of(
                new Transaction(TransactionType.DEPOSIT, 100),
                new Transaction(TransactionType.WITHDRAWAL, 50)));

        mock.perform(get("/api/ledger/transactions").param("accountId", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").exists())
                .andExpect(jsonPath("$[0].transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].transactionAmount").value(100))
                .andExpect(jsonPath("$[0].transactionTimestamp").exists())
                .andExpect(jsonPath("$[1].transactionId").exists())
                .andExpect(jsonPath("$[1].transactionType").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[1].transactionAmount").value(50))
                .andExpect(jsonPath("$[1].transactionTimestamp").exists());
    }

    //Negative Scenarios
    @Test
    void whenCreatingAccountWithBlankAppropriateErrorMessageIsReturned() throws Exception {
        Map<String, String> request = new HashMap<>();

        mock.perform(post("/api/ledger/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatingAccountWithNoNameAppropriateErrorMessageIsReturned() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("accountName", "");

        mock.perform(post("/api/ledger/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tryToDepositButAccountDoesntExistAppropriateErrorMessageIsReturned() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(null);

        DepositRequest request = new DepositRequest();
        request.setAccountId(UUID.randomUUID());
        request.setDepositAmount(10);

        mock.perform(post("/api/ledger/deposit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void tryToDepositANegativeAmountAppropriateErrorMessageIsReturned() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(new Account("John Doe"));

        DepositRequest request = new DepositRequest();
        request.setAccountId(UUID.randomUUID());
        request.setDepositAmount(-1);

        mock.perform(post("/api/ledger/deposit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tryToDepositZeroAmountToAccountAppropriateErrorMessageIsReturned() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(new Account("John Doe"));

        DepositRequest request = new DepositRequest();
        request.setAccountId(UUID.randomUUID());
        request.setDepositAmount(0);

        mock.perform(post("/api/ledger/deposit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tryToDepositWithNoAccountIdGivenAppropriateErrorMessageIsReturned() throws Exception {
        DepositRequest request = new DepositRequest();
        request.setDepositAmount(10);

        mock.perform(post("/api/ledger/deposit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tryToDepositWithNoDepositAmountGivenAppropriateErrorMessageIsReturned() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(new Account("John Doe"));
        DepositRequest request = new DepositRequest();
        request.setAccountId(UUID.randomUUID());

        mock.perform(post("/api/ledger/deposit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tryToWithdrawFromAccountThatDoesntExistAppropriateErrorMessageIsReturned() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(null);

        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountId(UUID.randomUUID());
        request.setWithdrawalAmount(10);

        mock.perform(post("/api/ledger/withdraw")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void tryToWithdrawMoreThanIsAccountAppropriateErrorMessageIsReturned() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(new Account("John Doe"));
        when(ledgerService.getBalance(any(UUID.class))).thenReturn(100L);

        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountId(UUID.randomUUID());
        request.setWithdrawalAmount(150);

        mock.perform(post("/api/ledger/withdraw")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tryToWithdrawWhenNoAccountIdIsGivenAppropriateErrorMessageIsReturned() throws Exception {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setWithdrawalAmount(10);

        mock.perform(post("/api/ledger/withdraw")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tryToWithdrawWhenNoWithdrawalAmountIsGivenAppropriateErrorMessageIsReturned() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(new Account("John Doe"));
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountId(UUID.randomUUID());

        mock.perform(post("/api/ledger/withdraw")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tryToGetBalanceFromAccountThatDoesntExistAppropriateErrorMessageIsReturned() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(null);

        mock.perform(get("/api/ledger/balance").param("accountId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void tryToGetBalanceSendingEmptyRequestAppropriateErrorMessageIsReturned() throws Exception {
        mock.perform(get("/api/ledger/balance").param("accountId", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tryToGetTransactionsForAccountThatDoesntExistAppropriateErrorMessageIsReturned() throws Exception {
        when(ledgerService.getAccount(any(UUID.class))).thenReturn(null);

        mock.perform(get("/api/ledger/transactions").param("accountId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void tryToGetTransactionsButNoAccountIdProvidedAppropriateErrorMessageIsReturned() throws Exception {

        mock.perform(get("/api/ledger/transactions").param("accountId", ""))
                .andExpect(status().isBadRequest());
    }
}
