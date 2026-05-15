Implement the accepted tiny-ledger plan.

Constraints:
- Java 25.
- Spring Boot 3.5.x.
- Spring MVC / servlet only.
- No WebFlux or reactive programming.
- No persistence outside in-memory structures.
- No authentication, queues, Kafka, containers, or external services.
- Money must use integer minor units.
- Keep the implementation easy to test and easy to change.
- Work on a non-main branch.
- Use Conventional Commit messages with short bodies.

After editing:
1. run `mvn test`;
2. run `node --test tests/hooks/codex-hooks.test.mjs tests/skills/skill-config.test.mjs`;
3. update README examples if observable behaviour changed;
4. update `openspec/specs/**` if accepted behaviour changed;
5. do not stage or commit `openspec/changes/**`.
