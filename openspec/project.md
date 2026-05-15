# Project: Picobooks

## Purpose

A tiny local ledger API for the Teya take-home assessment.

## Product boundary

The service records simple money transactions for accounts and exposes balance and transaction history.

It is not a production ledger, bank account platform, payment processor, reconciliation engine, or accounting system.

## Technical boundary

- Local runnable web application.
- Spring Boot 3 servlet/MVC API.
- Java 25 with virtual threads enabled.
- In-memory storage.
- No authentication or authorization.
- No persistent database.
- No distributed transaction guarantees.
- No external services.

## Spec workflow

Source-of-truth behaviour lives under `openspec/specs/**`.

Active proposal deltas under `openspec/changes/**` are local working artefacts.
They are intentionally gitignored and should be copied into PR descriptions before the corresponding source-of-truth spec is updated in the same PR.

## Terms

- **Account**: Aggregate root identified by a service-generated account identifier.
- **Ledger**: Value object held by one account; it contains accepted transactions and current balance.
- **Transaction**: Entity representing an accepted deposit or withdrawal.
- **Balance**: The current account position derived from accepted transactions.
- **Currency base units**: Integer representation of currency, such as pence for GBP.
