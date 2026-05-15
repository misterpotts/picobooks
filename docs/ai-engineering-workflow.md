# AI-Assisted Engineering Workflow

This repository uses a Codex-only harness to demonstrate bounded autonomy for a small financial-domain engineering exercise.

## Goals

- Improve delivery speed without weakening engineering controls.
- Keep behavioural intent, implementation, tests, and documentation aligned.
- Make AI-assisted changes reviewable by humans.
- Avoid hidden or unreviewed changes to behavioural specifications.

## Workflow

```text
intent
  -> non-main branch
  -> OpenSpec-style local delta
  -> implementation plan
  -> Codex-assisted implementation
  -> tests
  -> separate Codex review pass
  -> applied source-of-truth spec
  -> draft PR with commitlint-compatible title and verification evidence
  -> human review and merge
```

## Review surface

Active deltas are not committed. The pull request is the review surface for:

- behavioural delta;
- implementation summary;
- test evidence;
- assumptions;
- trade-offs;
- production evolution notes.

Agent commits and PR titles use Conventional Commits so reviewers can scan intent quickly and CI can reject unclear titles before review.

The repository remains the clean source of truth after merge because the applied behaviour lives under `openspec/specs/**`.

## Safety controls

- Project instructions live in `AGENTS.md`.
- Project-scoped Codex configuration lives in `.codex/config.toml`.
- Network access is disabled in the default workspace-write sandbox.
- Destructive command patterns are blocked by hook and rule examples.
- Stop hooks check for quality-gate drift before Codex presents work as complete.
- Hook and rule policies block direct pushes to `main`, force pushes, and agent-side PR merges.
- Local Codex run state and memory are gitignored.

## What this is not

This is not a production SDLC or enterprise AI control plane.

A production version would add centrally managed configuration, model/tool approvals, audit-log retention, SAST/dependency scanning, provenance controls, policy-as-code, data-classification controls, and organisation-specific incident/rollback procedures.
