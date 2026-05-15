# Elegant Objects Reference

Primary sources:
- https://www.elegantobjects.org/
- https://www.yegor256.com/2014/05/05/oop-alternative-to-utility-classes.html
- https://www.yegor256.com/2014/05/13/why-null-is-bad.html

Use this as pragmatic pressure, not as a blanket ban on Spring conventions.

## Checklist

- Prefer objects that expose behavior over data objects that force callers to make decisions.
- Avoid `null` returns from domain methods; choose exceptions, Null Objects, or explicit absence.
- Avoid static utility classes for domain behavior. Replace them with named objects or focused
  domain services that can be substituted in tests.
- Keep constructors simple. Use constructors or factories to make valid objects, not to perform IO,
  reach globals, or assemble complex graphs.
- Favor immutable value objects. If state must change, make the lifecycle and invariant explicit.
- Be skeptical of getters/setters that only exist so another class can perform the real behavior.

## Spring Boundary Exceptions

Accept these when they stay at the application boundary:
- DTOs, request/response records, and serialization-friendly shapes.
- Bean configuration, annotations, and framework-managed construction.
- Thin controllers that translate HTTP concerns into application calls.
- Validation annotations for input constraints before domain construction.

Do not use the boundary exception to justify domain objects that are just mutable data bags, static
helper collections, or nullable procedural APIs.
