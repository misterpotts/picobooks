<img src="assets/picobooks-logo.jpg">

# Picobooks

Picobooks is a deliberately small Java/Spring Boot ledger repository for the Teya take-home assessment.

The repository is being built through short, reviewable pull requests. This initial state establishes the repository purpose and the contribution rules before application code is introduced.

## Purpose

The final service should demonstrate a compact ledger implementation with clear domain rules, tests, and explicit trade-offs.

The implementation is expected to stay within the assessment scope:

- Java 25.
- Spring Boot 3.5.x.
- Spring MVC / servlet web.
- In-memory storage.
- No authentication, persistence, queues, Kafka, service discovery, containers, or deployment infrastructure unless the assessment explicitly requires it.

## Workflow

All non-trivial changes should go through a pull request.

Contributors and agents should:

1. create a short-lived non-main branch;
2. make the smallest coherent change;
3. use Conventional Commit messages;
4. open a non-draft pull request with a Conventional Commit title;
5. leave merge decisions to human review.

## Codex Harness

The initial Codex harness lives under `.codex/` and provides repo-local governance:

- project-scoped Codex configuration;
- command hooks for unsafe actions, PR workflow checks, and completion gates;
- repository execution rules;
- a PR-driven delivery skill for consistent agent behaviour.

Agents may open and update PRs for review, but they must not merge them.

## Conventional Commits

Commit messages and pull request titles must use Conventional Commits, for example:

```text
chore: add initial repository governance
feat: implement ledger transactions
fix: reject overdrawn withdrawals
```

Keep commit bodies short. Omit the body for simple commits; when a body is useful, keep it concise and wrap lines at 100 characters.

## CI

The CI workflow runs on pull requests and pushes to `main`. It enforces:

- passing Maven verification with `mvn --batch-mode verify`;
- at least 80% project-level line coverage through JaCoCo;
- Conventional Commit formatting for pull request titles and every commit in a pull request.

CI installs commitlint at runtime without committing a local Node package manifest or lockfile.
