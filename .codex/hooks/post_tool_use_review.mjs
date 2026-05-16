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

const event = await readEvent();
const cwd = String(event.cwd ?? process.cwd());
const command = commandFromEvent(event);
const exitCode = event?.tool_response?.exit_code;
const isMavenVerify = isMavenVerifyCommand(command);

if (isMavenVerify && typeof exitCode === "number") {
  const passed = exitCode === 0;
  recordTestRunMarker(cwd, command, exitCode, passed);
  if (passed) {
    recordSuccessMarker(cwd, command);
    process.exit(0);
  }
  additionalContext("PostToolUse", "The Maven verification command failed. Do not present the work as complete until tests and coverage pass or the failure is explicitly documented in the final response.");
  process.exit(0);
}

if (/spring-boot-starter-webflux|reactor-core/i.test(command)) {
  additionalContext("PostToolUse", "This assessment is constrained to Spring MVC / servlet. Re-check that no reactive dependency or WebFlux code was introduced.");
}
