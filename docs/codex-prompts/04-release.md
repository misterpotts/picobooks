Prepare the repository for review.

Before declaring completion:
1. run `mvn test`;
2. run `node --test tests/hooks/codex-hooks.test.mjs tests/skills/skill-config.test.mjs`;
3. ensure commit messages and PR title are commitlint-compatible;
4. ensure README examples match implemented behaviour;
5. ensure `openspec/specs/**` describes implemented behaviour;
6. ensure no `openspec/changes/**` files are staged;
7. ensure no WebFlux/reactive dependency or code was introduced;
8. create or update a draft PR with a Conventional Commit title;
9. produce a concise PR description containing:
   - intent;
   - OpenSpec delta;
   - implementation summary;
   - verification;
   - assumptions;
   - AI assistance disclosure.

Do not invent test results. If a command cannot be run, say exactly why.
Do not merge the PR. Human review and merge are required.
