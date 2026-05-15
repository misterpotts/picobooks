---
name: domain-object-testability
description: Use when planning/designing, implementing, or reviewing domain code, especially Java/Spring domain and application changes in Picobooks. Applies Domain-Driven Design modeling, Elegant Objects object design, and testability heuristics to keep domain objects behavior-rich, bounded, and easy to unit test.
---

# Domain Object Testability

Use this skill as a lifecycle checklist for Picobooks domain work. Keep Spring MVC controllers,
DTOs, validation annotations, configuration, and framework wiring pragmatic at boundaries; apply
stricter object-design scrutiny to domain and application code.

This skill complements `$pr-driven-delivery`, `$review-delivery-plan`, and
`$review-implementation`; it does not replace branch, commit, PR, or gate rules.

## Workflow

### Planning And Design

Identify the bounded context and the exact domain language before naming classes or methods.

Classify the model elements:
- Entities: identity and lifecycle matter.
- Value objects: equality is by value; favor immutability and validation at creation.
- Domain services: use only when behavior does not naturally belong to one entity or value object.
- Aggregates: choose boundaries that protect invariants without exposing object graph traversal.

Place behavior where the data, invariant, and domain phrase already live. Avoid designs where an
application service repeatedly extracts state from objects and performs domain decisions externally.

Plan test seams early. Domain behavior should be unit-testable without Spring, IO, clocks, random
numbers, environment state, or real repositories unless the seam itself is under test.

Read `references/domain-driven-design.md` when the main uncertainty is terminology, aggregate
boundaries, or whether a concept is an entity, value object, or service.

During plan review, require the proposed plan to identify the domain concepts, aggregate or service
boundaries, behavior placement, and unit-test seams for domain/application changes.

### Implementation

Favor behavior-rich objects with names and methods that read like the domain, not generic utilities.

Prefer immutable value objects. Validate required facts at construction or factory boundaries, and
keep constructors limited to assignment plus simple validation.

Do not return `null` from domain methods. Prefer a domain exception, a Null Object where meaningful,
or an explicit absence type such as `Optional` at application or boundary seams.

Inject collaborators explicitly. Avoid hidden global state, service locators, mutable static fields,
and singleton access from domain code.

Avoid utility dumping grounds. If shared behavior has domain meaning, give it a domain name and make
it an object or focused service; if it is framework glue, keep it near the boundary.

Avoid deep collaborator traversal. Ask a collaborator to do meaningful work instead of walking
through its internals to reach another object.

Keep Spring pragmatism explicit: controllers may be thin adapters, DTOs may be data carriers, and
validation annotations may express input constraints. Do not let those boundary compromises leak
into the domain model by default.

Read `references/elegant-objects.md` when deciding object shape, null handling, static helpers, or
Spring boundary exceptions. Read `references/testability.md` when tests are awkward or collaborators
are hard to substitute.

### Review

Check whether the change preserves ubiquitous language in package, class, method, and test names.

Check whether domain behavior stayed inside domain objects or intentional domain services rather
than drifting into controllers, DTOs, repository adapters, or procedural application services.

Check whether objects can be tested with ordinary unit tests: no Spring context, no hidden globals,
no constructor side effects, and no mandatory real infrastructure.

Check whether aggregate boundaries protect invariants and avoid exposing internals through getters
that exist only so another class can make domain decisions.

Check whether any EO, DDD, or testability compromise is explicit and local. Accept pragmatic Spring
boundary code, but require a short rationale for compromises inside domain/application code.

During implementation review, classify domain/object-design concerns as blocking when they make the
domain behavior ambiguous, hard to test without Spring, or dependent on hidden global state.
