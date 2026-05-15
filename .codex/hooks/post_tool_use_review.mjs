#!/usr/bin/env node
import { mkdirSync, writeFileSync } from "node:fs";
import { join } from "node:path";
import {
  additionalContext,
  commandFromEvent,
  isMavenTestCommand,
  readEvent,
  workspaceFingerprint,
} from "./hook_utils.mjs";

function recordSuccessMarker(cwd, command) {
  const markerDir = join(cwd, ".codex", "tmp");
  mkdirSync(markerDir, { recursive: true });
  writeFileSync(
    join(markerDir, "last-test-success.json"),
    `${JSON.stringify({
      command,
      fingerprint: workspaceFingerprint(cwd),
      timestamp: new Date().toISOString(),
    }, null, 2)}\n`,
    "utf8",
  );
}

const event = await readEvent();
const cwd = String(event.cwd ?? process.cwd());
const command = commandFromEvent(event);
const exitCode = event?.tool_response?.exit_code;
const isMavenTest = isMavenTestCommand(command);

if (isMavenTest && exitCode === 0) {
  recordSuccessMarker(cwd, command);
  process.exit(0);
}

if ((/\bmvn(?:w|\.cmd|w\.cmd)?\b/.test(command) || /[\\/]mvnw(?:\.cmd)?\b/.test(command)) && /\btest\b/.test(command) && exitCode !== undefined && exitCode !== 0) {
  additionalContext("PostToolUse", "The Maven test command failed. Do not present the work as complete until tests pass or the failure is explicitly documented.");
  process.exit(0);
}

if (/spring-boot-starter-webflux|reactor-core/i.test(command)) {
  additionalContext("PostToolUse", "This assessment is constrained to Spring MVC / servlet. Re-check that no reactive dependency or WebFlux code was introduced.");
}
