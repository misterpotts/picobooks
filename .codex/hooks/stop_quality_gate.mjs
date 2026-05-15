#!/usr/bin/env node
import { existsSync, readFileSync } from "node:fs";
import { join } from "node:path";
import {
  blockStop,
  inGitRepo,
  readEvent,
  relevantChangedFiles,
  repositoryConstraintViolations,
  trackedOpenSpecChanges,
  workspaceFingerprint,
  writeJson,
} from "./hook_utils.mjs";

function readMarker(cwd, filename) {
  const markerPath = join(cwd, ".codex", "tmp", filename);
  if (!existsSync(markerPath)) {
    return null;
  }
  try {
    return JSON.parse(readFileSync(markerPath, "utf8"));
  } catch {
    return null;
  }
}

function hasCurrentTestRun(cwd) {
  const fingerprint = workspaceFingerprint(cwd);
  const success = readMarker(cwd, "last-test-success.json");
  if (success && success.fingerprint === fingerprint) {
    return true;
  }
  const run = readMarker(cwd, "last-test-run.json");
  return Boolean(run && run.fingerprint === fingerprint);
}

const event = await readEvent();
const cwd = String(event.cwd ?? process.cwd());

if (!inGitRepo(cwd)) {
  writeJson({ continue: true, systemMessage: "Quality gate hook skipped because this is not yet a git repository." });
  process.exit(0);
}

if (trackedOpenSpecChanges(cwd)) {
  blockStop("Active OpenSpec deltas under openspec/changes/** are tracked. Remove them from staging/history, copy the delta into the PR body, and apply accepted behaviour under openspec/specs/**.");
  process.exit(0);
}

const constraintViolations = repositoryConstraintViolations(cwd);
if (constraintViolations.length > 0) {
  blockStop(`Repository constraints are unmet:\n- ${constraintViolations.join("\n- ")}`);
  process.exit(0);
}

const relevantFiles = relevantChangedFiles(cwd);
if (relevantFiles.length > 0 && !hasCurrentTestRun(cwd)) {
  blockStop("Relevant files changed but `mvn verify` has not been run after the latest relevant edits. Run `mvn verify` for the current workspace state; the response may complete on either a pass or a documented failure.");
  process.exit(0);
}

writeJson({ continue: true });
