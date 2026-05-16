#!/usr/bin/env node
import {
  additionalContext,
  commandFromEvent,
  isMavenVerifyCommand,
  readEvent,
  writeMarker,
  workspaceFingerprint,
} from "./hook_utils.mjs";

function recordTestRunMarker(cwd, command, exitCode, passed) {
  writeMarker(cwd, "last-test-run.json", {
    command,
    exitCode,
    passed,
    fingerprint: workspaceFingerprint(cwd),
    timestamp: new Date().toISOString(),
  });
}

function recordSuccessMarker(cwd, command) {
  writeMarker(cwd, "last-test-success.json", {
    command,
    fingerprint: workspaceFingerprint(cwd),
    timestamp: new Date().toISOString(),
  });
}

function markerPersistenceFailure(error) {
  if (Array.isArray(error.markerFailures) && error.markerFailures.length > 0) {
    return error.markerFailures.join("; ");
  }
  return error.code ?? error.message;
}

const event = await readEvent();
const cwd = String(event.cwd ?? process.cwd());
const command = commandFromEvent(event);
const exitCode = event?.tool_response?.exit_code;
const isMavenVerify = isMavenVerifyCommand(command);

if (isMavenVerify && typeof exitCode === "number") {
  const passed = exitCode === 0;
  try {
    recordTestRunMarker(cwd, command, exitCode, passed);
    if (passed) {
      recordSuccessMarker(cwd, command);
    }
  } catch (error) {
    additionalContext(
      "PostToolUse",
      `mvn verify exited ${exitCode} but the hook could not persist its marker to either .codex/tmp or the OS tmpdir fallback (${markerPersistenceFailure(error)}). The stop-gate will block this work as unverified; please make one of the marker directories writable for this identity.`,
    );
    process.exit(0);
  }
  if (passed) {
    process.exit(0);
  }
  additionalContext("PostToolUse", "The Maven verification command failed. Do not present the work as complete until tests and coverage pass or the failure is explicitly documented in the final response.");
  process.exit(0);
}

if (/spring-boot-starter-webflux|reactor-core/i.test(command)) {
  additionalContext("PostToolUse", "This assessment is constrained to Spring MVC / servlet. Re-check that no reactive dependency or WebFlux code was introduced.");
}
