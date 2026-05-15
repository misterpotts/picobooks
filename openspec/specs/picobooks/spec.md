# Picobooks Specification

## API Contract

The service SHALL expose a small JSON HTTP API for one-account-at-a-time ledger operations.

### `GET /health`

Returns service health.

```json
{
  "status": "UP"
}
```

### `POST /accounts/{accountId}/transactions`

Records an accepted deposit or withdrawal for one account and returns the created ledger entry with
HTTP `201 Created`.

Request:

```json
{
  "type": "DEPOSIT",
  "amount": {
    "amountMinor": 10000,
    "currency": "GBP"
  },
  "reference": "Initial deposit"
}
```

Response:

```json
{
  "transactionId": "11111111-1111-1111-1111-111111111111",
  "accountId": "merchant-123",
  "type": "DEPOSIT",
  "amount": {
    "amountMinor": 10000,
    "currency": "GBP"
  },
  "resultingBalance": {
    "amountMinor": 10000,
    "currency": "GBP"
  },
  "reference": "Initial deposit",
  "occurredAt": "2026-05-15T12:00:00Z"
}
```

### `GET /accounts/{accountId}/balance`

Returns the current balance for one account with HTTP `200 OK`.

```json
{
  "accountId": "merchant-123",
  "balance": {
    "amountMinor": 10000,
    "currency": "GBP"
  }
}
```

### `GET /accounts/{accountId}/transactions`

Returns accepted ledger entries for one account in append order with HTTP `200 OK`.

```json
[
  {
    "transactionId": "11111111-1111-1111-1111-111111111111",
    "accountId": "merchant-123",
    "type": "DEPOSIT",
    "amount": {
      "amountMinor": 10000,
      "currency": "GBP"
    },
    "resultingBalance": {
      "amountMinor": 10000,
      "currency": "GBP"
    },
    "reference": "Initial deposit",
    "occurredAt": "2026-05-15T12:00:00Z"
  }
]
```

### Error Responses

Invalid client requests return HTTP `400 Bad Request` with a stable error body:

```json
{
  "code": "invalid_ledger_request",
  "message": "Withdrawal would overdraw account",
  "occurredAt": "2026-05-15T12:00:00Z"
}
```

## Wire Rules

- `accountId` SHALL be a non-blank path value.
- `type` SHALL be `DEPOSIT` or `WITHDRAWAL`.
- Transaction `amount.amountMinor` SHALL be a positive integer minor-unit amount.
- Balance `amountMinor` SHALL be a non-negative integer minor-unit amount.
- `currency` SHALL be accepted case-insensitively and normalized to uppercase three-letter form.
- `reference` MAY be omitted or blank; accepted entries SHALL return the stored reference value.
- `transactionId` SHALL be a UUID.
- `occurredAt` SHALL be an ISO-8601 instant.
- Floating point money values SHALL NOT be accepted.

## Behavior Requirements

### Requirement: Run as a local web application

The service SHALL expose HTTP APIs and run locally without requiring optional infrastructure beyond
Java, Maven, and project dependencies.

#### Scenario: Service health can be checked

- WHEN a client sends `GET /health`
- THEN the service responds successfully
- AND the response body is `{ "status": "UP" }`

### Requirement: Record a deposit

The service SHALL allow a client to record a positive deposit for an account.

#### Scenario: Deposit accepted

- WHEN a client submits a deposit for an account
- AND the amount is positive
- AND the currency is valid for that account
- THEN the account ledger appends an immutable ledger entry
- AND the account balance increases by the deposit amount

### Requirement: Record a withdrawal

The service SHALL allow a client to record a positive withdrawal for an account when sufficient
funds are available.

#### Scenario: Withdrawal accepted

- WHEN a client submits a withdrawal for an account
- AND the amount is positive
- AND the account has sufficient funds
- AND the currency is valid for that account
- THEN the account ledger appends an immutable ledger entry
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
- THEN the service returns accepted ledger entries for that account in append order
- AND each entry includes transaction type, amount, resulting balance, reference, and occurrence time

### Requirement: Represent money in minor units

The service SHALL represent money as integer minor units with a three-letter currency code.

#### Scenario: Floating point values are not accepted

- WHEN a client submits an amount
- THEN the amount is represented as an integer `amountMinor`
- AND not as a floating point decimal

#### Scenario: Non-positive amounts are rejected

- WHEN a client submits zero or a negative amount
- THEN the service rejects the request
- AND no ledger entry is recorded

### Requirement: Use a single currency per account

The service SHALL treat the first accepted transaction as establishing the account currency.

#### Scenario: First transaction establishes currency

- WHEN the first transaction for an account is accepted
- THEN that transaction's currency becomes the account currency

#### Scenario: Later transaction with different currency is rejected

- GIVEN an account has an established currency
- WHEN a later transaction uses a different currency
- THEN the service rejects the request
- AND no ledger entry is recorded

## Domain Object Definitions

- **AccountId**: Value object identifying exactly one account. It rejects blank values.
- **Money**: Value object for a positive transaction amount and normalized three-letter currency.
- **Balance**: Value object for a non-negative current account position and currency.
- **TransactionType**: Enumeration of `DEPOSIT` and `WITHDRAWAL`.
- **LedgerEntry**: Immutable accepted transaction record containing transaction id, account id,
  transaction type, amount, resulting balance, optional reference, and occurrence time.
- **AccountLedger**: Aggregate for exactly one account. It owns currency consistency, balance
  calculation, sufficient-funds checks, and append-only entry creation.

## Solution Space

### Object Graph

The API controller maps JSON into an application command for one `AccountId`. The application
service loads exactly one `AccountLedger`, asks that aggregate to accept or reject the transaction,
and saves only that account's resulting ledger state. Read operations load only the requested
account's ledger state and project it into API responses.

No domain object named or behaving as a global ledger SHALL coordinate multiple accounts. No global
ledger aggregate SHALL own balances, entries, or transaction ordering across accounts.

### In-Memory Data Model

The in-memory store SHALL be partitioned by `AccountId`. Each account partition owns its ordered
collection of `LedgerEntry` records and its derived or cached current `Balance`.

Account creation is implicit: the first accepted transaction creates that account's partition and
establishes its currency. Balance and history reads for one account SHALL NOT require scanning other
accounts.

### Concurrency And Contention Decision

Contention SHALL be possible only at the account level. Concurrent operations for different accounts
MUST NOT share a domain lock, aggregate lock, or global ledger lock.

A per-account synchronization strategy is acceptable for this in-memory assessment implementation.
The service does not provide cross-account transactions, transfers, global balances, global history,
or global ordering guarantees.
