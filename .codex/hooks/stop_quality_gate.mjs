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

function validTestMarker(cwd) {
  const markerPath = join(cwd, ".codex", "tmp", "last-test-success.json");
  if (!existsSync(markerPath)) {
    return false;
  }
  try {
    const marker = JSON.parse(readFileSync(markerPath, "utf8"));
    return marker.fingerprint === workspaceFingerprint(cwd);
  } catch {
    return false;
  }
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
if (relevantFiles.length > 0 && !validTestMarker(cwd)) {
  blockStop("Relevant files changed but no current successful `mvn test` marker exists for this workspace state. Run `mvn test` after the latest relevant edits, or explicitly document why it cannot be run.");
  process.exit(0);
}

writeJson({ continue: true });
