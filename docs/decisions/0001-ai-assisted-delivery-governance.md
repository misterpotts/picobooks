# ADR-0001: AI-Assisted Delivery Governance

## Status

Accepted for skeleton.

## Context

The assessment is intentionally small, but the Principal Engineer signal should include safe, reviewable use of AI-assisted engineering.

OpenSpec normally keeps active change proposals in a local changes area. For this assessment, the desired workflow is to keep those active deltas out of the repository, place them in PR descriptions, and commit the resulting applied source-of-truth spec alongside the code change.

## Decision

Use OpenSpec-style deltas during development, but do not commit active `openspec/changes/**` folders.

Instead:

1. The active delta is copied into the pull request description.
2. The same pull request applies the resulting source-of-truth specification under `openspec/specs/**`.
3. Code, tests, README examples, and applied spec changes are reviewed together.
4. Codex local run state, temporary files, and memory are not committed.

## Rationale

Active deltas are review artefacts, not long-lived product artefacts. The pull request is the natural control point for intent, implementation, and verification evidence. The repository remains clean after merge while retaining current approved behaviour in `openspec/specs/**`.

## Consequences

### Positive

- Reviewers see the behavioural delta and implementation together.
- The repository does not accumulate stale proposal folders.
- The final spec remains durable and searchable.
- The workflow demonstrates governed AI-assisted development without overbuilding the assessment.

### Negative

- The active delta workspace is not available after merge except through the PR history.
- Developers must remember to copy deltas into PR descriptions.
- Tooling cannot rely on committed `openspec/changes/**` folders.

## Follow-ups

A production-grade version would automate PR body generation from the local delta and fail CI if implemented behaviour, source-of-truth spec, and PR evidence diverged.
