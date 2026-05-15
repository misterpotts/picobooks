---
name: pr-driven-delivery
description: Use for this repository when planning, implementing, reviewing, releasing, creating or editing PRs, writing commits, or responding to review comments. Enforces branch-based delivery, Conventional Commit messages and PR titles, short commit bodies, non-draft PRs, local gates, and human review before merge.
---

# PR-Driven Delivery

Use this skill for repository changes in this project.

## Required Workflow

1. Work on a non-main branch.
2. For behaviour changes, create or update a local OpenSpec delta and later copy it into the PR body.
3. Implement the smallest useful change, keeping README examples and `openspec/specs/**` aligned.
4. Run available quality gates before release. At minimum, Maven verification and commitlint must pass in CI.
5. Commit with Conventional Commits only.
6. Open or update a non-draft PR with a Conventional Commit title.
7. Stop before merge; human review and merge are required.

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
