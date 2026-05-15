import assert from "node:assert/strict";
import { mkdirSync, mkdtempSync, readFileSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join, resolve } from "node:path";
import { spawnSync } from "node:child_process";
import { test } from "node:test";

const repoRoot = resolve(import.meta.dirname, "..", "..");
const hooksDir = join(repoRoot, ".codex", "hooks");
const validPom = `<?xml version="1.0" encoding="UTF-8"?>
<project>
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.14</version>
  </parent>
  <properties>
    <java.version>25</java.version>
  </properties>
  <dependencies>
    <dependency>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
  </dependencies>
</project>
`;

function runHook(script, event, cwd = repoRoot) {
  const result = spawnSync("node", [join(hooksDir, script)], {
    cwd,
    input: JSON.stringify(event),
    encoding: "utf8",
    windowsHide: true,
  });
  assert.equal(result.status, 0, result.stderr);
  return result.stdout.trim() ? JSON.parse(result.stdout) : {};
}

function git(args, cwd) {
  const result = spawnSync("git", args, { cwd, encoding: "utf8", windowsHide: true });
  assert.equal(result.status, 0, result.stderr);
  return result;
}

function makeRepo() {
  const dir = mkdtempSync(join(tmpdir(), "codex-hooks-"));
  git(["init"], dir);
  git(["config", "user.email", "test@example.invalid"], dir);
  git(["config", "user.name", "Hook Test"], dir);
  mkdirSync(join(dir, "src", "main", "resources"), { recursive: true });
  writeFileSync(join(dir, ".gitignore"), ".codex/tmp/**\n.codex/runs/**\n.codex/memory/**\n");
  writeFileSync(join(dir, "pom.xml"), validPom);
  writeFileSync(join(dir, "src", "main", "resources", "application.yaml"), "spring:\n  main:\n    web-application-type: servlet\n  threads:\n    virtual:\n      enabled: true\n");
  git(["add", "."], dir);
  git(["commit", "-m", "initial"], dir);
  return dir;
}

test("pre tool hook denies destructive commands with the supported permission shape", () => {
  const payload = runHook("pre_tool_use_policy.mjs", {
    tool_input: { command: "git reset --hard" },
  });

  assert.equal(payload.hookSpecificOutput.hookEventName, "PreToolUse");
  assert.equal(payload.hookSpecificOutput.permissionDecision, "deny");
});

test("pre tool hook denies direct pushes to main", () => {
  const payload = runHook("pre_tool_use_policy.mjs", {
    tool_input: { command: "git push origin main" },
  });

  assert.equal(payload.hookSpecificOutput.permissionDecision, "deny");
  assert.match(payload.hookSpecificOutput.permissionDecisionReason, /main/);
});

test("pre tool hook denies PR merges", () => {
  const payload = runHook("pre_tool_use_policy.mjs", {
    tool_input: { command: "gh pr merge 123 --squash" },
  });

  assert.equal(payload.hookSpecificOutput.permissionDecision, "deny");
  assert.match(payload.hookSpecificOutput.permissionDecisionReason, /merge/i);
});

test("pre tool hook denies non-conventional commit messages", () => {
  const payload = runHook("pre_tool_use_policy.mjs", {
    tool_input: { command: "git commit -m \"update workflow\"" },
  });

  assert.equal(payload.hookSpecificOutput.permissionDecision, "deny");
  assert.match(payload.hookSpecificOutput.permissionDecisionReason, /Conventional Commits/);
});

test("pre tool hook allows conventional commit messages", () => {
  const payload = runHook("pre_tool_use_policy.mjs", {
    tool_input: { command: "git commit -m \"chore: update workflow\"" },
  });

  assert.deepEqual(payload, {});
});

test("pre tool hook denies non-conventional PR titles", () => {
  const payload = runHook("pre_tool_use_policy.mjs", {
    tool_input: { command: "gh pr create --title \"Update workflow\"" },
  });

  assert.equal(payload.hookSpecificOutput.permissionDecision, "deny");
  assert.match(payload.hookSpecificOutput.permissionDecisionReason, /PR title/);
});

test("pre tool hook allows conventional PR titles", () => {
  const payload = runHook("pre_tool_use_policy.mjs", {
    tool_input: { command: "gh pr create --title \"chore: update workflow\"" },
  });

  assert.deepEqual(payload, {});
});

test("permission request hook denies system package installs", () => {
  const payload = runHook("permission_request_policy.mjs", {
    tool_input: { command: "winget install something" },
  });

  assert.equal(payload.hookSpecificOutput.hookEventName, "PermissionRequest");
  assert.equal(payload.hookSpecificOutput.decision.behavior, "deny");
});

test("post hook records a content-addressed Maven test marker", () => {
  const dir = makeRepo();
  runHook("post_tool_use_review.mjs", {
    cwd: dir,
    tool_input: { command: "mvn test" },
    tool_response: { exit_code: 0 },
  }, dir);

  const marker = JSON.parse(readFileSync(join(dir, ".codex", "tmp", "last-test-success.json"), "utf8"));
  assert.equal(marker.command, "mvn test");
  assert.match(marker.fingerprint, /^[a-f0-9]{64}$/);
});

test("post hook does not treat echoed Maven text as a test run", () => {
  const dir = makeRepo();
  runHook("post_tool_use_review.mjs", {
    cwd: dir,
    tool_input: { command: "echo mvn test" },
    tool_response: { exit_code: 0 },
  }, dir);

  const result = spawnSync("git", ["status", "--porcelain=v1", ".codex/tmp/last-test-success.json"], { cwd: dir, encoding: "utf8", windowsHide: true });
  assert.equal(result.stdout.trim(), "");
});

test("stop hook rejects stale Maven markers after relevant edits", () => {
  const dir = makeRepo();
  runHook("post_tool_use_review.mjs", {
    cwd: dir,
    tool_input: { command: "mvn test" },
    tool_response: { exit_code: 0 },
  }, dir);

  writeFileSync(join(dir, "README.md"), "changed after tests\n");
  const payload = runHook("stop_quality_gate.mjs", { cwd: dir }, dir);

  assert.equal(payload.decision, "block");
  assert.match(payload.reason, /`mvn test` has not been run after the latest relevant edits/);
});

test("post hook records a Maven test run marker on failure", () => {
  const dir = makeRepo();
  runHook("post_tool_use_review.mjs", {
    cwd: dir,
    tool_input: { command: "mvn test" },
    tool_response: { exit_code: 1 },
  }, dir);

  const marker = JSON.parse(readFileSync(join(dir, ".codex", "tmp", "last-test-run.json"), "utf8"));
  assert.equal(marker.command, "mvn test");
  assert.equal(marker.exitCode, 1);
  assert.equal(marker.passed, false);
  assert.match(marker.fingerprint, /^[a-f0-9]{64}$/);
});

test("stop hook allows a failed Maven run that matches the current workspace", () => {
  const dir = makeRepo();
  writeFileSync(join(dir, "README.md"), "edit before failing test run\n");
  runHook("post_tool_use_review.mjs", {
    cwd: dir,
    tool_input: { command: "mvn test" },
    tool_response: { exit_code: 1 },
  }, dir);

  const payload = runHook("stop_quality_gate.mjs", { cwd: dir }, dir);
  assert.equal(payload.continue, true);
  assert.equal(payload.decision, undefined);
});

test("stop hook rejects a stale failed Maven run after later relevant edits", () => {
  const dir = makeRepo();
  writeFileSync(join(dir, "README.md"), "first change\n");
  runHook("post_tool_use_review.mjs", {
    cwd: dir,
    tool_input: { command: "mvn test" },
    tool_response: { exit_code: 1 },
  }, dir);

  writeFileSync(join(dir, "README.md"), "second change after failed test\n");
  const payload = runHook("stop_quality_gate.mjs", { cwd: dir }, dir);
  assert.equal(payload.decision, "block");
  assert.match(payload.reason, /`mvn test` has not been run after the latest relevant edits/);
});

test("stop hook rejects reactive dependencies", () => {
  const dir = makeRepo();
  writeFileSync(join(dir, "pom.xml"), validPom.replace("spring-boot-starter-web", "spring-boot-starter-webflux"));

  const payload = runHook("stop_quality_gate.mjs", { cwd: dir }, dir);

  assert.equal(payload.decision, "block");
  assert.match(payload.reason, /WebFlux or Reactor/);
});
