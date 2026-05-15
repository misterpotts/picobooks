# AGENTS.md

## Purpose

This repository is the Picobooks Java/Spring Boot ledger project for the Teya take-home assessment.

Use Codex and other agents as governed engineering assistants. Keep intent reviewable, make small changes, and preserve human review as the acceptance boundary.

## Assessment Constraints

- Use Java 25.
- Use Spring Boot 3.5.x.
- Use Spring MVC / servlet web.
- Do not introduce Spring WebFlux, Reactor-first designs, or reactive programming.
- Enable virtual threads with `spring.threads.virtual.enabled=true`.
- Use in-memory storage only.
- Keep the application runnable locally with Maven.
- Do not add auth, persistence, queues, Kafka, containers, service discovery, or deployment infrastructure for the assessment.

## PR-Driven Workflow

- Do all non-trivial work on a non-main branch.
- Follow the staged agent workflow: plan, independently review the plan, iterate until there is no blocking feedback, implement, independently review the implementation, and iterate until there is no blocking feedback.
- Use the repo-local `$pr-driven-delivery` skill for repository changes, `$review-delivery-plan` for plan review, `$review-implementation` for implementation review, and `$domain-object-testability` when domain or application modeling is involved.
- Open a pull request for review before merging.
- Agents may create and edit non-draft PRs, but must not merge PRs or approve their own work.
- Do not push directly to `main`.
- Treat AI-generated changes as draft until reviewed by a human.

## Boundary And Test Design

- Treat API request DTOs as guarded boundary objects; validate required fields and simple shape in constructors or equivalent boundary types.
- Do not knowingly pass `null` to another function. Reject missing data at the boundary or represent absence explicitly.
- Keep error-code vocabulary canonical in `openspec/specs/**` before or alongside implementation.
- Use Spring Boot integration tests for representative flows that prove wiring and serialization. Put edge cases in faster controller, DTO, domain, or application tests.

## Conventional Commits

- Use Conventional Commits for every commit message.
- Use a commitlint-compatible Conventional Commit title for every PR.
- Allowed types are `build`, `chore`, `ci`, `docs`, `feat`, `fix`, `perf`, `refactor`, `revert`, `style`, and `test`.
- Keep commit bodies short. Omit bodies for simple commits; when needed, keep body/footer lines concise and wrapped at 100 characters.

## Initial Quality Gate

Before opening or updating a PR, ensure the PR title and commit messages are Conventional Commit compatible.
Run `mvn verify` so tests pass and JaCoCo confirms at least 80% project-level line coverage.

CI enforces these gates on pull requests and pushes to `main`.
