# feat: implement tiny ledger API

## Intent

Implement the tiny ledger behaviours requested by the assessment:

- record deposits;
- record withdrawals;
- view current balance;
- view transaction history.

The implementation should remain deliberately small: Java 25, Spring Boot 3 MVC, virtual threads enabled, in-memory storage, no authentication, no persistence, no queues, no external infrastructure.

## OpenSpec Delta

The active local delta is intentionally not committed under `openspec/changes/**`. It is copied here as the PR review surface.

```openspec
## ADDED Requirements

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
```

## Applied Spec

The accepted behaviour is committed under:

```text
openspec/specs/tiny-ledger/spec.md
```

## Implementation Summary

- Spring MVC controllers for money movement, balance, and transaction history.
- Application service for ledger use cases.
- Domain rules for money, currency, movement type, and resulting balance.
- In-memory append-only repository.
- Validation and API error mapping.

## Verification

```bash
mvn test
```

Manual examples:

```bash
curl -X POST http://localhost:8080/accounts/merchant-123/movements \
  -H "Content-Type: application/json" \
  -d '{"type":"DEPOSIT","amount":{"amountMinor":10000,"currency":"GBP"},"reference":"Initial deposit"}'

curl http://localhost:8080/accounts/merchant-123/balance
curl http://localhost:8080/accounts/merchant-123/movements
```

## Assumptions

- Amounts are integer minor units.
- First movement establishes account currency.
- Withdrawals cannot overdraw.
- In-memory state is lost on restart.
- Authentication and authorisation are out of scope.

## AI Assistance Disclosure

Codex was used through a local governed harness for planning, implementation support, and review. The harness uses repository instructions, sandboxed execution, local hooks, command rules, ignored local run state, and a separate review pass. All generated changes were reviewed before inclusion.

This PR title and agent commit messages use Conventional Commits. Commit bodies are omitted or kept short unless extra review context is needed.
