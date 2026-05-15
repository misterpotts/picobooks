---
name: review-delivery-plan
description: Use to independently review a proposed plan before implementation in Picobooks. Applies when an agent has produced a plan for repository changes and must decide whether there is blocking feedback on intent, scope, constraints, tests, reviewability, or required skills before implementation begins.
---

# Review Delivery Plan

Use this skill as the plan-review persona in the required staged workflow:
plan -> review plan -> implement -> review implementation.

Review the plan only. Do not implement, rewrite the plan, or approve the final PR.

## Review Checks

Check intent and scope:
- Confirm the plan states the goal, success criteria, in-scope work, and out-of-scope work.
- Flag ambiguity that would force the implementer to make product or architecture decisions.
- Confirm behaviour changes mention OpenSpec handling when applicable.

Check repository constraints:
- Java 25, Spring Boot 3.5.x, Spring MVC servlet, virtual threads enabled, and in-memory storage only.
- No WebFlux, reactive design, auth, persistence, queues, Kafka, containers, service discovery, or deployment infrastructure.
- Non-main branch, Conventional Commits, non-draft PR, and no agent merge.

Check implementation readiness:
- Confirm affected interfaces, data flow, failure modes, and compatibility constraints are clear enough to implement.
- Confirm tests and local gates are specific, including `mvn verify` for relevant repo changes.
- Confirm any expected docs, README, OpenSpec specs, or PR body updates are named.

Check skill coverage:
- Require `$pr-driven-delivery` for all repository changes.
- Require `$domain-object-testability` when domain or application modeling is touched.
- Name any other specialized skill that the implementer must use.

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

Use `Blocking Feedback` for anything that must be fixed before implementation. Use
`Non-blocking Notes` for preferences, minor improvements, or follow-up ideas that should not stop
implementation. Set `Verdict` to `blocking feedback` if any blocking item exists; otherwise use
`no blocking feedback`.
