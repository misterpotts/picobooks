---
name: pr-driven-delivery
description: Use for this repository when planning, reviewing plans, implementing, reviewing implementations, releasing, creating or editing PRs, writing commits, or responding to review comments. Enforces staged plan-review-implement-review delivery, branch-based delivery, Conventional Commit messages and PR titles, short commit bodies, non-draft PRs, local gates, and human review before merge.
---

# PR-Driven Delivery

Use this skill for repository changes in this project.

## Required Workflow

1. Work on a non-main branch.
2. Produce a concrete implementation plan before editing tracked files.
3. Get an independent plan review with `$review-delivery-plan`.
4. Iterate on the plan until the reviewer verdict is `no blocking feedback`.
5. Implement the smallest useful change.
6. Get an independent implementation review with `$review-implementation`.
7. Iterate on the implementation until the reviewer verdict is `no blocking feedback`.
8. Run available quality gates before release. At minimum, Maven verification and commitlint must pass in CI.
9. Commit with Conventional Commits only.
10. Open or update a non-draft PR with a Conventional Commit title.
11. Stop before merge; human review and merge are required.

## Stage Rules

- Treat plan review and implementation review as required agent review gates before PR release.
- Use `$domain-object-testability` during planning, implementation, and review when domain or application modeling changes.
- For behaviour changes, create or update a local OpenSpec delta before implementation, then copy the delta into the PR body and keep accepted `openspec/specs/**` aligned.
- Do not begin implementation while plan review has blocking feedback.
- Do not create or update a PR while implementation review has blocking feedback.
- Human PR review remains the final acceptance boundary; agent review is not merge approval.

## Agent Roles

- Planner/implementer: use `$pr-driven-delivery`, plus domain or task-specific skills needed for the change.
- Plan reviewer: use `$review-delivery-plan`; review the plan only and report blocking feedback.
- Implementation reviewer: use `$review-implementation`; review the diff and verification evidence only.
- Human reviewer: review the PR and decide merge readiness.

## Commit Rules

- Use `type(scope): subject` or `type: subject`.
- Allowed types: `build`, `chore`, `ci`, `docs`, `feat`, `fix`, `perf`, `refactor`, `revert`, `style`, `test`.
- Keep subjects imperative, lower-case where natural, and under 100 characters.
- Omit commit bodies for simple changes.
- When a body is needed, keep it short and factual; wrap body/footer lines at 100 characters.
- Install local git hooks with `scripts/install-git-hooks.sh` when hooks are not already configured.
- CI runs Maven verification, commitlint against PR commits, and commitlint against the PR title.

## PR Rules

- Use a PR title that also passes commitlint, for example `feat: implement ledger movements`.
- Include: intent, OpenSpec delta, implementation summary, verification, assumptions, and AI assistance disclosure.
- Use `gh pr create --title "<type>: <subject>" --body-file <file>` for new PRs.
- Use `gh pr edit --title "<type>: <subject>" --body-file <file>` for updates.
- Do not run `gh pr merge` or merge locally.

## PR Description Template

Use the following template when writing a PR description body. Keep each section concise: prefer short bullets, concrete evidence, and "Not applicable" when a section does not apply.

```markdown
## Intent
<!-- 1-3 bullets explaining the user-facing or reviewer-facing purpose of the change. -->

-

## OpenSpec Delta
<!-- Behaviour changes: paste the local OpenSpec delta here. Non-behaviour changes: say why no delta was needed. -->

## Applied Spec
<!-- Behaviour changes: list committed openspec/specs/** paths. Non-behaviour changes: say "Not applicable". -->

## Implementation Summary
<!-- 3-6 bullets describing outcomes and important design choices, not a raw file list. -->

-

## Verification
<!-- List commands run and relevant manual checks. Include failures or skipped checks with reasons. -->

- [ ] `mvn verify`
- [ ] `node --test tests/hooks/codex-hooks.test.mjs tests/skills/skill-config.test.mjs`
- [ ] `node .codex/hooks/check_repository_constraints.mjs`

## Assumptions and Trade-offs
<!-- Call out deliberate omissions, scope boundaries, or assessment constraints that affect review. -->

## AI Assistance Disclosure

Codex helped with this change under the repository's governed PR-driven delivery workflow.
The resulting code, tests, documentation, and PR description were reviewed before submission.
```

## Review Comments

- Treat review comments as requested changes, not as approval.
- Make follow-up commits with Conventional Commit messages.
- Keep PR bodies current when assumptions, verification, or scope changes.
