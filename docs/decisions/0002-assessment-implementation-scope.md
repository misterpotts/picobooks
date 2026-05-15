# ADR-0002: Assessment Implementation Scope

## Status

Accepted for assessment implementation.

## Context

This repository is a small take-home ledger service, not a production platform.

The implementation should show correctness, clear domain rules, and reviewable trade-offs without adding infrastructure that the assessment does not ask for.

## Decision

Keep the implementation small and focused on the ledger behaviours.

The ledger implementation must:

- use integer minor units for money;
- reject negative or zero amounts;
- let the first movement establish account currency;
- reject later movements in a different currency;
- reject withdrawals that would overdraw;
- expose transaction history with resulting balances;
- keep storage in-memory;
- include domain, service, and API tests;
- document assumptions and trade-offs.

Do not add authentication, persistence, queues, Kafka, observability stacks, service discovery, containers, or deployment infrastructure unless explicitly required by the assessment.

## Rationale

The strongest assessment signal is a correct, well-tested ledger with explicit constraints and omissions.

Adding production infrastructure would increase surface area, hide the core domain choices, and make the implementation harder to review within the assessment scope.

## Consequences

### Positive

- The code stays small enough to review quickly.
- Domain behaviour remains the centre of the implementation.
- Tests can focus on money, balance, currency, overdraw, and history rules.
- Production-grade omissions are deliberate rather than accidental.

### Negative

- Data is lost on restart.
- There is no authentication or authorisation boundary.
- There is no production observability, deployment, or scaling story.
- In-memory storage means concurrent update behaviour remains intentionally limited.

## Follow-ups

A production-grade version would require separate decisions and specifications for persistence, transactions, idempotency, authentication, observability, deployment, and operational support.
