import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { join, resolve } from "node:path";
import { test } from "node:test";

const repoRoot = resolve(import.meta.dirname, "..");

test("PR title lint is limited to pull request events", () => {
  const workflow = readFileSync(join(repoRoot, ".github", "workflows", "ci.yml"), "utf8").replace(/\r\n/g, "\n");
  const conventionalCommitsJob = workflow.match(/  conventional-commits:[\s\S]*?(?=\n  [a-zA-Z0-9_-]+:|\n?$)/)?.[0] ?? "";

  assert.match(workflow, /\n  push:\n    branches:\n      - main\n/);
  assert.match(workflow, /\n  pull_request:\n/);
  assert.match(conventionalCommitsJob, /if: github\.event_name == 'pull_request'/);
  assert.match(conventionalCommitsJob, /name: Lint PR title/);
  assert.match(conventionalCommitsJob, /github\.event\.pull_request\.title/);
});
