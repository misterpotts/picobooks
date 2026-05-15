import assert from "node:assert/strict";
import { existsSync, readFileSync } from "node:fs";
import { join, resolve } from "node:path";
import { test } from "node:test";

const repoRoot = resolve(import.meta.dirname, "..", "..");
const skillRoot = join(repoRoot, ".codex", "skills", "pr-driven-delivery");

test("pr-driven-delivery skill has required frontmatter and workflow terms", () => {
  const skillPath = join(skillRoot, "SKILL.md");
  assert.equal(existsSync(skillPath), true);

  const text = readFileSync(skillPath, "utf8").replace(/\r\n/g, "\n");
  assert.match(text, /^---\nname: pr-driven-delivery\n/s);
  assert.match(text, /description: .*Conventional Commit/s);
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

test("local workflow uses git hooks without a repo package.json", () => {
  assert.equal(existsSync(join(repoRoot, "package.json")), false);
  assert.equal(existsSync(join(repoRoot, ".githooks", "commit-msg")), true);
  assert.equal(existsSync(join(repoRoot, ".githooks", "pre-push")), true);
  assert.equal(existsSync(join(repoRoot, "scripts", "install-git-hooks.sh")), true);
});
