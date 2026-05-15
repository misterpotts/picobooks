# ADR-0001: AI-Assisted Delivery Governance

## Status

Accepted.

## Context

The assessment is intentionally small, and intended to include safe, reviewable use of AI-assisted engineering.
## Decision

Use OpenSpec-style deltas during development, but do not commit active `openspec/changes/**` folders.

Instead:

1. The active delta is copied into the pull request description.
2. The same pull request applies the resulting source-of-truth specification under `openspec/specs/**`.
3. Code, tests, README examples, and applied spec changes are reviewed together.
4. Codex local run state, temporary files, and memory are not committed.

## Rationale

Active deltas are review artefacts, not long-lived product artefacts.
The pull request is the natural control point for intent, implementation, and verification evidence.
The repository remains clean after merge while retaining current approved behaviour in `openspec/specs/**`.

## Consequences

### Positive

- Reviewers see the behavioural delta and implementation together.
- The repository does not accumulate stale proposal folders.
- The final spec remains durable and searchable.
- The workflow demonstrates governed AI-assisted development without overbuilding the assessment.

### Negative

- This approach diverges from the standard OpenSpec workflow, and may require some effort for contributors to adopt.
- The active delta workspace is not available after merge except through the PR history.
- Developers must remember to copy deltas into PR descriptions.
- Tooling cannot rely on committed `openspec/changes/**` folders.

## Follow-ups

A production-grade version would automate PR body generation from the local delta and fail CI if implemented behaviour, source-of-truth spec, and PR evidence diverged.
