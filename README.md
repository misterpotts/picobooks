<img src="assets/picobooks-logo.jpg">

# Picobooks

Picobooks is a compact Java/Spring Boot ledger service for the Teya take-home assessment.
It exposes a small JSON API for account creation, account-scoped deposits and withdrawals,
current balance reads, and transaction history.

The service is intentionally scoped for local assessment review:

- Java 25 is required.
- Spring Boot 3.5.x.
- Spring MVC on the servlet stack.
- Virtual threads enabled with `spring.threads.virtual.enabled=true`.
- In-memory storage only.
- Maven local run and verification.
- No authentication, persistence, queues, Kafka, containers, service discovery, or deployment
  infrastructure.

## Design Summary

Picobooks demonstrates a deliberately small ledger implementation:

- HTTP request records guard required fields and simple shape at the boundary.
- Each `AccountLedger` owns one account's currency, balance, and accepted transaction history.
- Money is represented as integer currency base units, avoiding floating point arithmetic.
- Withdrawals are rejected before they can make an account balance negative.
- Storage is in-memory by assessment constraint, with account-local update serialization.

## Run Locally

Install Java 25 and Maven, then run:

```sh
mvn spring-boot:run
```

The API listens on `http://localhost:8080` by default.

```sh
curl -i http://localhost:8080/health
```

Run the assessment quality gate with:

```sh
mvn verify
```

`mvn verify` runs the test suite and checks project-level JaCoCo line coverage is at least 80%.

## API

Amounts are integer currency base units, such as pence for GBP. Currency values are normalized
to uppercase three-letter codes. Deposits and withdrawals share the same transaction endpoint and
are distinguished by `type`.

After creating an account, use the returned `accountId` as `ACCOUNT_ID` in the examples below.

### Create Account

`POST /accounts`

```sh
curl -i -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "currency": "GBP"
  }'
```

Example `201 Created` response:

```json
{
  "accountId": "0198f3a5-7b1c-7d2e-8f90-123456789abc",
  "createdAt": "2026-05-15T12:00:00Z"
}
```

Use the returned identifier in later examples:

```sh
ACCOUNT_ID="0198f3a5-7b1c-7d2e-8f90-123456789abc"
```

### Deposit

`POST /accounts/{accountId}/transactions`

```sh
curl -i -X POST "http://localhost:8080/accounts/$ACCOUNT_ID/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "DEPOSIT",
    "amount": {
      "value": 10000,
      "currency": "GBP"
    },
    "reference": "Initial deposit"
  }'
```

Example `201 Created` response:

```json
{
  "transactionId": "0198f3a6-45ef-7a10-8b22-89abcdef0123",
  "accountId": "0198f3a5-7b1c-7d2e-8f90-123456789abc",
  "type": "DEPOSIT",
  "amount": {
    "value": 10000,
    "currency": "GBP"
  },
  "resultingBalance": {
    "value": 10000,
    "currency": "GBP"
  },
  "reference": "Initial deposit",
  "occurredAt": "2026-05-15T12:00:00Z"
}
```

### Withdrawal

`POST /accounts/{accountId}/transactions`

```sh
curl -i -X POST "http://localhost:8080/accounts/$ACCOUNT_ID/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "WITHDRAWAL",
    "amount": {
      "value": 2500,
      "currency": "GBP"
    },
    "reference": "Supplier payment"
  }'
```

Example `201 Created` response:

```json
{
  "transactionId": "0198f3a6-cb11-7856-9210-89abcdef0123",
  "accountId": "0198f3a5-7b1c-7d2e-8f90-123456789abc",
  "type": "WITHDRAWAL",
  "amount": {
    "value": 2500,
    "currency": "GBP"
  },
  "resultingBalance": {
    "value": 7500,
    "currency": "GBP"
  },
  "reference": "Supplier payment",
  "occurredAt": "2026-05-15T12:01:00Z"
}
```

### Current Balance

`GET /accounts/{accountId}/balance`

```sh
curl -i "http://localhost:8080/accounts/$ACCOUNT_ID/balance"
```

Example `200 OK` response:

```json
{
  "accountId": "0198f3a5-7b1c-7d2e-8f90-123456789abc",
  "balance": {
    "value": 7500,
    "currency": "GBP"
  }
}
```

### Transaction History

`GET /accounts/{accountId}/transactions`

```sh
curl -i "http://localhost:8080/accounts/$ACCOUNT_ID/transactions"
```

Example `200 OK` response:

```json
[
  {
    "transactionId": "0198f3a6-45ef-7a10-8b22-89abcdef0123",
    "accountId": "0198f3a5-7b1c-7d2e-8f90-123456789abc",
    "type": "DEPOSIT",
    "amount": {
      "value": 10000,
      "currency": "GBP"
    },
    "resultingBalance": {
      "value": 10000,
      "currency": "GBP"
    },
    "reference": "Initial deposit",
    "occurredAt": "2026-05-15T12:00:00Z"
  },
  {
    "transactionId": "0198f3a6-cb11-7856-9210-89abcdef0123",
    "accountId": "0198f3a5-7b1c-7d2e-8f90-123456789abc",
    "type": "WITHDRAWAL",
    "amount": {
      "value": 2500,
      "currency": "GBP"
    },
    "resultingBalance": {
      "value": 7500,
      "currency": "GBP"
    },
    "reference": "Supplier payment",
    "occurredAt": "2026-05-15T12:01:00Z"
  }
]
```

### Insufficient Funds

Withdraw more than the current balance to see the business rejection path.

```sh
curl -i -X POST "http://localhost:8080/accounts/$ACCOUNT_ID/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "WITHDRAWAL",
    "amount": {
      "value": 999999,
      "currency": "GBP"
    },
    "reference": "Overdraw attempt"
  }'
```

Example `409 Conflict` response:

```json
{
  "code": "insufficient_funds",
  "message": "Withdrawal would overdraw account",
  "occurredAt": "2026-05-15T12:02:00Z"
}
```

Other validation failures use the same error shape with a stable `code`, human-readable `message`,
and `occurredAt` timestamp.

## Assumptions And Trade-offs

- The ledger is account-scoped. There is no global ledger aggregate, cross-account transfer API, or
  double-entry posting model in this assessment implementation.
- `InMemoryLedgerRepository` keeps data only for the lifetime of the process. Restarting the service
  clears accounts and transactions.
- Each account has one currency, set at account creation. Later transactions must use the same
  currency.
- Money uses integer base units and rejects floating point values. Currency validation is deliberately
  limited to three-letter shape and normalization; it is not a full ISO-4217 registry with scale rules.
- The in-memory repository serializes updates per account with `ConcurrentHashMap.compute`, which is
  enough for local assessment concurrency but not a horizontally scaled deployment.
- Request DTOs guard required fields and simple shape at the HTTP boundary before application or
  domain logic runs.
- The repository remains public for submission in line with Teya guidance.

## Production Evolution

For real financial workloads, Picobooks would need deliberate production changes rather than just
more endpoints:

- Replace `InMemoryLedgerRepository` with durable storage and transactional posting semantics, using
  serializable isolation or account-row locking where the datastore requires it.
- Move from the current single-account transaction endpoint to true double-entry postings that debit
  one account and credit another atomically.
- Add idempotency keys to every mutating endpoint so client or queue retries cannot double-post.
- Store an append-only audit or event history separately from any materialized balance projection.
- Replace simple currency string validation with explicit ISO-4217 currency, scale, and rounding
  policy.
- Preserve the current per-account serialization guarantee across service instances, using row locks,
  queues, actors, or another account-partitioned strategy.
- Add account-id sharding and an outbox pattern for downstream events when horizontal scale matters.
- Introduce read-side projections if balance or history queries dominate write traffic.
- Add structured logs, metrics for posting latency and rejection rates by error code, and distributed
  tracing.
- Split health checks into liveness and readiness, with readiness covering the durable store and
  other required dependencies.
- Apply backpressure and rate limiting at the API edge.
- Add authentication, authorization, account-level scopes, and controls for any PII or PCI-adjacent
  data introduced later.
- Define retention and erasure semantics, plus reconciliation jobs against settlement, statements, or
  other external systems.
- Add schema migrations, progressive deployment, backups, point-in-time recovery, and tested restore
  procedures.

## Contributor Workflow

All non-trivial changes should go through a short-lived branch and a pull request. The repository uses
Conventional Commits for commit messages and PR titles, and CI runs Maven verification, coverage, and
commitlint checks.

Agents may open and update pull requests for review, but merge decisions remain with a human reviewer.

## AI-Assisted Workflow

This repository was developed with AI assistance under a governed, PR-driven workflow. The assistant
helped draft plans, implement small changes, run local verification, and prepare PRs; human review
remains the acceptance boundary.
