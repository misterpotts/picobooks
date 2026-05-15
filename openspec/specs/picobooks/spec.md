# Picobooks Specification

## Requirements

### Requirement: Run as a local web application

The service SHALL expose HTTP APIs and run locally without requiring optional infrastructure beyond Java, Maven, and project dependencies.

#### Scenario: Service health can be checked

- WHEN a client sends `GET /health`
- THEN the service responds successfully
- AND the response identifies the application as a skeleton until the ledger implementation is completed.

### Requirement: Record a deposit

The service SHALL allow a client to record a positive deposit for an account.

#### Scenario: Deposit accepted

- WHEN a client submits a deposit for an account
- AND the amount is positive
- AND the currency is valid for that account
- THEN the service records a ledger entry
- AND the account balance increases by the deposit amount

### Requirement: Record a withdrawal

The service SHALL allow a client to record a positive withdrawal for an account when sufficient funds are available.

#### Scenario: Withdrawal accepted

- WHEN a client submits a withdrawal for an account
- AND the amount is positive
- AND the account has sufficient funds
- AND the currency is valid for that account
- THEN the service records a ledger entry
- AND the account balance decreases by the withdrawal amount

#### Scenario: Withdrawal rejected for insufficient funds

- WHEN a client submits a withdrawal that would make the account balance negative
- THEN the service rejects the request
- AND no ledger entry is recorded

### Requirement: View current balance

The service SHALL allow a client to view the current balance for an account.

#### Scenario: Balance returned

- WHEN a client requests the balance for an account with accepted ledger entries
- THEN the service returns the current balance
- AND the balance is expressed in minor units and currency

### Requirement: View transaction history

The service SHALL allow a client to view transaction history for an account.

#### Scenario: History returned

- WHEN a client requests transaction history for an account
- THEN the service returns accepted ledger entries for that account
- AND each entry includes movement type, amount, resulting balance, reference, and occurrence time.

### Requirement: Represent money in minor units

The service SHALL represent money as integer minor units with a three-letter currency code.

#### Scenario: Floating point values are not accepted

- WHEN a client submits an amount
- THEN the amount is represented as an integer `amountMinor`
- AND not as a floating point decimal.

#### Scenario: Non-positive amounts are rejected

- WHEN a client submits zero or a negative amount
- THEN the service rejects the request
- AND no ledger entry is recorded.

### Requirement: Use a single currency per account

The service SHALL treat the first accepted movement as establishing the account currency.

#### Scenario: First movement establishes currency

- WHEN the first movement for an account is accepted
- THEN that movement's currency becomes the account currency.

#### Scenario: Later movement with different currency is rejected

- GIVEN an account has an established currency
- WHEN a later movement uses a different currency
- THEN the service rejects the request
- AND no ledger entry is recorded.
