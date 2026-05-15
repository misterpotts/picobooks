import assert from "node:assert/strict";
import { existsSync, readFileSync } from "node:fs";
import { join, resolve } from "node:path";
import { test } from "node:test";

const repoRoot = resolve(import.meta.dirname, "..", "..");
const skillRoot = join(repoRoot, ".codex", "skills", "pr-driven-delivery");
const domainSkillRoot = join(repoRoot, ".codex", "skills", "domain-object-testability");
const planReviewSkillRoot = join(repoRoot, ".codex", "skills", "review-delivery-plan");
const implementationReviewSkillRoot = join(repoRoot, ".codex", "skills", "review-implementation");

test("pr-driven-delivery skill has required frontmatter and workflow terms", () => {
  const skillPath = join(skillRoot, "SKILL.md");
  assert.equal(existsSync(skillPath), true);

  const text = readFileSync(skillPath, "utf8").replace(/\r\n/g, "\n");
  assert.match(text, /^---\nname: pr-driven-delivery\n/s);
  assert.match(text, /description: .*Conventional Commit/s);
  assert.match(text, /staged plan-review-implement-review delivery/);
  assert.match(text, /non-main branch/);
  assert.match(text, /draft PR/);
  assert.match(text, /Do not run `gh pr merge`/);
});

test("pr-driven-delivery skill exposes agent metadata", () => {
  const metadataPath = join(skillRoot, "agents", "openai.yaml");
  assert.equal(existsSync(metadataPath), true);

  const text = readFileSync(metadataPath, "utf8");
  assert.match(text, /display_name: "PR Driven Delivery"/);
  assert.match(text, /default_prompt: "Use \$pr-driven-delivery/);
});

test("pr-driven-delivery skill includes a comprehensive terse PR template", () => {
  const skillPath = join(skillRoot, "SKILL.md");
  const text = readFileSync(skillPath, "utf8");

  assert.match(text, /## PR Description Template/);
  assert.match(text, /## Intent/);
  assert.match(text, /## OpenSpec Delta/);
  assert.match(text, /## Applied Spec/);
  assert.match(text, /## Implementation Summary/);
  assert.match(text, /## Verification/);
  assert.match(text, /## Assumptions and Trade-offs/);
  assert.match(text, /## AI Assistance Disclosure/);
  assert.match(text, /Keep each section concise/);
});

test("pr-driven-delivery requires staged agent review before release", () => {
  const skillPath = join(skillRoot, "SKILL.md");
  const text = readFileSync(skillPath, "utf8");

  assert.match(text, /Produce a concrete implementation plan before editing tracked files/);
  assert.match(text, /Get an independent plan review with `\$review-delivery-plan`/);
  assert.match(text, /reviewer verdict is `no blocking feedback`/);
  assert.match(text, /Get an independent implementation review with `\$review-implementation`/);
  assert.match(text, /Do not begin implementation while plan review has blocking feedback/);
  assert.match(text, /Do not create or update a PR while implementation review has blocking feedback/);
  assert.match(text, /Human PR review remains the final acceptance boundary/);
  assert.match(text, /Use `\$domain-object-testability`/);
});

test("repository agent guidance captures boundary and test-design rules", () => {
  const text = readFileSync(join(repoRoot, "AGENTS.md"), "utf8");

  assert.match(text, /## Boundary And Test Design/);
  assert.match(text, /API request DTOs as guarded boundary objects/);
  assert.match(text, /Do not knowingly pass `null`/);
  assert.match(text, /error-code vocabulary canonical/);
  assert.match(text, /Spring Boot integration tests for representative flows/);
  assert.match(text, /edge cases in faster controller, DTO, domain, or application tests/);
});

test("pr-driven-delivery captures repeatable review learnings", () => {
  const skillPath = join(skillRoot, "SKILL.md");
  const text = readFileSync(skillPath, "utf8");

  assert.match(text, /repeatable repository rule/);
  assert.match(text, /AGENTS\.md/);
  assert.match(text, /skill, hook, or test configuration/);
});

test("domain-object-testability skill has required frontmatter and metadata", () => {
  const skillPath = join(domainSkillRoot, "SKILL.md");
  assert.equal(existsSync(skillPath), true);

  const text = readFileSync(skillPath, "utf8").replace(/\r\n/g, "\n");
  assert.match(text, /^---\nname: domain-object-testability\n/s);
  assert.match(text, /description: .*planning\/designing, implementing, or reviewing domain code/s);
  assert.match(text, /Domain-Driven Design modeling/);
  assert.match(text, /Elegant Objects object design/);
  assert.match(text, /testability heuristics/);

  const metadataPath = join(domainSkillRoot, "agents", "openai.yaml");
  assert.equal(existsSync(metadataPath), true);

  const metadata = readFileSync(metadataPath, "utf8");
  assert.match(metadata, /display_name: "Domain Object Testability"/);
  assert.match(metadata, /short_description: "Design testable domain objects"/);
  assert.match(metadata, /default_prompt: "Use \$domain-object-testability/);
});

test("domain-object-testability skill covers the full domain change lifecycle", () => {
  const skillPath = join(domainSkillRoot, "SKILL.md");
  const text = readFileSync(skillPath, "utf8");

  assert.match(text, /### Planning And Design/);
  assert.match(text, /bounded context/);
  assert.match(text, /ubiquitous language|domain language/);
  assert.match(text, /Entities/);
  assert.match(text, /Value objects/);
  assert.match(text, /Domain services/);
  assert.match(text, /Aggregates/);
  assert.match(text, /test seams/);

  assert.match(text, /### Implementation/);
  assert.match(text, /behavior-rich objects/);
  assert.match(text, /immutable value objects/);
  assert.match(text, /Do not return `null`/);
  assert.match(text, /Inject collaborators explicitly/);
  assert.match(text, /hidden global state/);
  assert.match(text, /utility dumping grounds/);
  assert.match(text, /deep collaborator traversal/);
  assert.match(text, /Do not knowingly pass `null`/);
  assert.match(text, /guarded DTO construction/);
  assert.match(text, /Jackson or Spring exceptions/);

  assert.match(text, /### Review/);
  assert.match(text, /Spring pragmatism/);
  assert.match(text, /domain behavior stayed inside domain objects/);
  assert.match(text, /explicit and local/);
  assert.match(text, /API request DTOs/);
  assert.match(text, /deliberate `null` forwarding as blocking/);
  assert.match(text, /\$review-delivery-plan/);
  assert.match(text, /\$review-implementation/);
  assert.match(text, /During plan review/);
  assert.match(text, /During implementation review/);
});

test("domain-object-testability skill includes focused primary-source references", () => {
  const referencesRoot = join(domainSkillRoot, "references");
  const expectedReferences = [
    "elegant-objects.md",
    "testability.md",
    "domain-driven-design.md",
  ];

  for (const reference of expectedReferences) {
    assert.equal(existsSync(join(referencesRoot, reference)), true);
  }

  const elegantObjects = readFileSync(join(referencesRoot, "elegant-objects.md"), "utf8");
  assert.match(elegantObjects, /https:\/\/www\.elegantobjects\.org\//);
  assert.match(elegantObjects, /yegor256\.com\/2014\/05\/05\/oop-alternative-to-utility-classes/);
  assert.match(elegantObjects, /yegor256\.com\/2014\/05\/13\/why-null-is-bad/);
  assert.match(elegantObjects, /Spring Boundary Exceptions/);

  const testability = readFileSync(join(referencesRoot, "testability.md"), "utf8");
  assert.match(testability, /github\.com\/mhevery\/guide-to-testable-code/);
  assert.match(testability, /Constructors do work/);
  assert.match(testability, /deep collaborator traversal|digging through several getters/);

  const domainDrivenDesign = readFileSync(join(referencesRoot, "domain-driven-design.md"), "utf8");
  assert.match(domainDrivenDesign, /martinfowler\.com\/bliki\/DomainDrivenDesign/);
  assert.match(domainDrivenDesign, /bounded context/);
  assert.match(domainDrivenDesign, /ubiquitous language/);
});

test("review-delivery-plan skill defines the plan reviewer persona", () => {
  const skillPath = join(planReviewSkillRoot, "SKILL.md");
  assert.equal(existsSync(skillPath), true);

  const text = readFileSync(skillPath, "utf8").replace(/\r\n/g, "\n");
  assert.match(text, /^---\nname: review-delivery-plan\n/s);
  assert.match(text, /description: .*review a proposed plan before implementation/s);
  assert.match(text, /plan -> review plan -> implement -> review implementation/);
  assert.match(text, /Review the plan only/);
  assert.match(text, /intent and scope/);
  assert.match(text, /repository constraints/);
  assert.match(text, /implementation readiness/);
  assert.match(text, /skill coverage/);
  assert.match(text, /request DTO validation/);
  assert.match(text, /missing-body handling/);
  assert.match(text, /stable error-code vocabulary/);
  assert.match(text, /representative Spring Boot integration flows/);
  assert.match(text, /faster controller, DTO, domain, or application tests/);
  assert.match(text, /\$pr-driven-delivery/);
  assert.match(text, /\$domain-object-testability/);
  assert.match(text, /do not plan tests that assert specific words exist in specs/i);
  assert.match(text, /human review is the regression gate for spec prose/i);
  assert.match(text, /## Blocking Feedback/);
  assert.match(text, /## Non-blocking Notes/);
  assert.match(text, /## Verdict/);
  assert.match(text, /no blocking feedback/);
  assert.match(text, /blocking feedback/);

  const metadata = readFileSync(join(planReviewSkillRoot, "agents", "openai.yaml"), "utf8");
  assert.match(metadata, /display_name: "Review Delivery Plan"/);
  assert.match(metadata, /default_prompt: "Use \$review-delivery-plan/);
});

test("review-implementation skill defines the implementation reviewer persona", () => {
  const skillPath = join(implementationReviewSkillRoot, "SKILL.md");
  assert.equal(existsSync(skillPath), true);

  const text = readFileSync(skillPath, "utf8").replace(/\r\n/g, "\n");
  assert.match(text, /^---\nname: review-implementation\n/s);
  assert.match(text, /description: .*review completed repository changes before PR creation or update/s);
  assert.match(text, /plan -> review plan -> implement -> review implementation/);
  assert.match(text, /Review the implemented changes and verification evidence/);
  assert.match(text, /intent fit/);
  assert.match(text, /correctness and maintainability/);
  assert.match(text, /repository constraints/);
  assert.match(text, /verification/);
  assert.match(text, /request DTOs or equivalent boundary types validate/);
  assert.match(text, /knowingly passes `null`/);
  assert.match(text, /documented in the accepted spec/);
  assert.match(text, /representative flows rather than every edge case/);
  assert.match(text, /faster controller, DTO, domain, or application tests/);
  assert.match(text, /\$domain-object-testability/);
  assert.match(text, /Do not require or add word-assertion tests for OpenSpec\/spec prose/);
  assert.match(text, /human review is the\s+regression gate for spec text/);
  assert.match(text, /## Blocking Feedback/);
  assert.match(text, /## Non-blocking Notes/);
  assert.match(text, /## Verdict/);
  assert.match(text, /no blocking feedback/);
  assert.match(text, /blocking feedback/);

  const metadata = readFileSync(join(implementationReviewSkillRoot, "agents", "openai.yaml"), "utf8");
  assert.match(metadata, /display_name: "Review Implementation"/);
  assert.match(metadata, /default_prompt: "Use \$review-implementation/);
});

test("local workflow uses git hooks without a repo package.json", () => {
  assert.equal(existsSync(join(repoRoot, "package.json")), false);
  assert.equal(existsSync(join(repoRoot, ".githooks", "commit-msg")), true);
  assert.equal(existsSync(join(repoRoot, ".githooks", "pre-push")), true);
  assert.equal(existsSync(join(repoRoot, "scripts", "install-git-hooks.sh")), true);
});
