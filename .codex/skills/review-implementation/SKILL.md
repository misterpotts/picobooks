---
name: review-implementation
description: Use to independently review completed repository changes before PR creation or update in Picobooks. Applies after implementation and before release to check diffs, tests, constraints, skill adherence, reviewability, and whether blocking feedback remains.
---

# Review Implementation

Use this skill as the implementation-review persona in the required staged workflow:
plan -> review plan -> implement -> review implementation.

Review the implemented changes and verification evidence. Do not merge, approve the PR, or make
follow-up edits as part of the review.

## Review Checks

Check intent fit:
- Confirm the diff implements the approved plan without unreviewed scope expansion.
- Flag missing requirements, changed public behaviour, or incomplete docs/spec updates.
- Confirm trade-offs are explicit where the plan or repo constraints require them.

Check correctness and maintainability:
- Review for bugs, regressions, fragile edge cases, and unclear ownership boundaries.
- Confirm code follows existing package, test, and naming conventions.
- For domain/application changes, apply `$domain-object-testability` and flag weak domain language,
  misplaced behaviour, hidden global state, null-prone APIs, or hard-to-test design.

Check repository constraints:
- Java 25, Spring Boot 3.5.x, Spring MVC servlet, virtual threads enabled, and in-memory storage only.
- No WebFlux, reactive design, auth, persistence, queues, Kafka, containers, service discovery, or deployment infrastructure.
- Conventional Commit subject and PR title are suitable.

Check verification:
- Confirm focused tests cover the change and `mvn verify` ran after the latest relevant edits.
- Confirm skill/config changes have appropriate Node tests and skill validation where applicable.
- Do not require or add word-assertion tests for OpenSpec/spec prose; human review is the
  regression gate for spec text.
- Treat missing, stale, or failed verification as blocking unless the failure is explicitly documented
  and outside the implemented change.

## Output Format

Return exactly these sections:

```markdown
## Blocking Feedback
- None

## Non-blocking Notes
- None

## Verdict
no blocking feedback
```

Use file and line references for blocking findings when possible. Set `Verdict` to
`blocking feedback` if any blocking item exists; otherwise use `no blocking feedback`.
