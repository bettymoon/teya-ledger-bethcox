# teya-ledger-bethcox

## How to Run
This code is written in Java 17.

Please open the project root (teya-ledger-bethcox) and run:
```
mvn clean package
```
This should succeed, once this has run the following:
```
java -jar api/target/api-1.0.0-SNAPSHOT.jar
```

This will run the API and it can be connected to from your local machine. 

## Example calls

### Create Account

Example API POST URL:
```
http://localhost:8080/api/ledger/create
```
Please replace the port number if your local machine chooses to run on a different port.

Example Request Body:
```
{
  "accountName": "John Doe"
}
```

Sending this as a POST Request you should yield a similar result:
```
{
  "accountId": "78227c6d-1c13-4716-827f-ed8eace0706b",
  "accountName": "John Doe",
  "balance": 0,
  "accountTransactions": []
}
```

Now that you have your Account Id you can use the other endpoints.

### Deposit Money

Example API POST URL:
```
http://localhost:8080/api/ledger/deposit
```

Example Request Body:
```
{
  "accountId": "78227c6d-1c13-4716-827f-ed8eace0706b",
  "depositAmount": 1000
}
```

Example Response:
```
{
  "balance": 1000
}
```

### Withdraw Money
Example API POST URL:
```
http://localhost:8080/api/ledger/withdraw
```

Example Request Body:
```
{
  "accountId": "78227c6d-1c13-4716-827f-ed8eace0706b",
  "withdrawalAmount": 50
}
```

Example Response:
```
{
  "balance": 950
}
```

### Get Balance
Example API GET URL:
```
http://localhost:8080/api/ledger/balance
```
Example header value:
```
accountId:78227c6d-1c13-4716-827f-ed8eace0706b
```

Example Response:
```
{
  "balance": 950
}
```

### Get Transactions
Example API GET URL:
```
http://localhost:8080/api/ledger/transactions
```

Example header value:
```
accountId:78227c6d-1c13-4716-827f-ed8eace0706b
```

Example Response:
```
[
  {
    "transactionId": "33ecfade-21c0-42fe-95a8-1f36a192efc2",
    "transactionType": "DEPOSIT",
    "transactionAmount": 1000,
    "transactionTimestamp": "2026-09-09T19:25:40.277813"
  },
  {
    "transactionId": "6b77e7f2-2838-4903-a464-167d423a71ce",
    "transactionType": "WITHDRAWAL",
    "transactionAmount": 50,
    "transactionTimestamp": "2026-09-09T19:26:14.1353695"
  }
]
```

# Assumptions & Need to Fixes
I made this as a bare basic API, without a lot of authorisation and safety marking. It assumes that there is additional security methods to prevent malicious usage. 

I have also assumed that the accounts are not able to have overdrafts and therefore withdrawals cannot exceed the available balance and the balance cannot be negative. If the account cannot be found, I return a negative long, which is treated the same as a null would. 

I have used UUID to generate identifiers for Accounts and Transactions, but I would assume that a company has some way of generating these internally.

For the GET calls I have passed the AccountId as a header rather than a parameter, this avoid the URL from being used maliciously. 

I would also like to create custom exceptions which give end users detailed messages as to why their calls failed. For the time being I have relied on the normal HTTP Responses to respond to incorrect requests.

For this reason I have done most of the error handling in the Controller itself rather than the Service class. This way it returns basic response errors. 

This is also bare basic Java and does not include any other language that would be necessary for running in a production environment. 

# Testing

I followed TDD and the main testing for the Controller can be found in LedgerControllerIT however, ideally I would have also create Unit Tests for the Service class. I would also implement e2e testing using a tool like Xray to ensure that there is backwards compatibility going forward.

