#!/usr/bin/env node
import {
  blockStop,
  inGitRepo,
  readEvent,
  readMarkerCandidates,
  relevantChangedFiles,
  repositoryConstraintViolations,
  trackedOpenSpecChanges,
  workspaceFingerprint,
  writeJson,
} from "./hook_utils.mjs";

function hasCurrentTestRun(cwd) {
  const fingerprint = workspaceFingerprint(cwd);
  const successMarkers = readMarkerCandidates(cwd, "last-test-success.json");
  if (successMarkers.some((marker) => marker.fingerprint === fingerprint)) {
    return true;
  }
  const runMarkers = readMarkerCandidates(cwd, "last-test-run.json");
  return runMarkers.some((marker) => marker.fingerprint === fingerprint);
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
