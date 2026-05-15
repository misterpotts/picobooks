# Domain-Driven Design Reference

Primary source:
- https://martinfowler.com/bliki/DomainDrivenDesign.html

Use this when deciding how Picobooks concepts should be named, grouped, and tested.

## Modeling Checklist

- Define the bounded context before generalizing names across the application.
- Use ubiquitous language in production code and tests; avoid technical aliases for domain concepts.
- Prefer a rich domain model when ledger rules, movement rules, balances, or invariants are involved.
- Classify concepts deliberately:
  - Entity: identity and lifecycle matter.
  - Value object: the value itself matters; equality should be by value.
  - Domain service: behavior spans concepts and does not naturally belong to one object.
  - Aggregate: consistency boundary that protects invariants.
- Keep aggregate boundaries small enough for simple tests and large enough to enforce invariants.
- Put calculations and rule checks next to the concepts they depend on.
- Let application services coordinate use cases; do not let them become the main home of domain
  rules unless the rule is genuinely orchestration.

## Picobooks Prompts

- What is the ledger term a reviewer or assessor would expect here?
- Which object owns the invariant being changed?
- Is this rule about one movement, one account, one ledger, or a cross-object policy?
- Does the test describe domain behavior or implementation plumbing?
- If the model changes later, which class name will make the change obvious?
