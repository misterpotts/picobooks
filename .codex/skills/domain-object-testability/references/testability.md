# Testability Reference

Primary sources:
- https://github.com/mhevery/guide-to-testable-code
- https://github.com/mhevery/guide-to-testable-code/blob/main/flaw-constructor-does-work.md

Use this when a design is hard to unit test or requires a Spring context for ordinary domain logic.

## Warning Signs

- Constructors do work beyond assignment and simple validation.
- Constructors allocate important collaborators, call static methods, branch heavily, perform IO,
  read time/random/environment state, or build complex object graphs.
- Objects are passed in mainly to reach other objects through call chains.
- Names such as `Context`, `Environment`, `Manager`, or `Container` hide real responsibilities.
- Domain code depends on singletons, mutable static fields, registries, or service locators.
- A class summary naturally needs "and" because it owns multiple unrelated responsibilities.
- Tests need reflection, sleeps, global cleanup, ordered execution, or a Spring context to exercise
  simple domain rules.

## Remediation Prompts

- Can the collaborator be injected as an interface or small domain role?
- Can object creation move to a factory while the domain object receives ready dependencies?
- Can time, random values, IDs, or environment reads be passed in explicitly?
- Can a method ask one collaborator for behavior instead of digging through several getters?
- Can the class split along separate invariants or lifecycle responsibilities?
- Can the test construct the object directly and assert observable domain behavior?
