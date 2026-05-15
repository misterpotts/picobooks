#!/usr/bin/env node
import { commandFromEvent, denyPermissionRequest, readEvent } from "./hook_utils.mjs";

const DENIED_ESCALATIONS = [
  [/\b(apt|apt-get|brew|choco|winget)\s+install\b/i, "Installing system packages is outside the assessment workflow."],
  [/\b(curl|wget)\b.+\|\s*(sh|bash|pwsh|powershell)\b/i, "Pipe-to-shell network execution is not allowed."],
  [/\b(iwr|irm|Invoke-WebRequest|Invoke-RestMethod)\b.+\|\s*(iex|Invoke-Expression|pwsh|powershell)\b/i, "Pipe-to-shell network execution is not allowed."],
  [/\bgit\s+push\s+--force\b/, "Force pushing is not appropriate for this reviewable assessment workflow."],
  [/\bgit\s+push\b(?=.*\borigin\b)(?=.*\bmain\b)/, "Direct pushes to main are not allowed. Open or update a PR branch for human review."],
  [/\bgh\s+pr\s+merge\b/, "PR merging is reserved for human review."],
];

const event = await readEvent();
const command = commandFromEvent(event);

for (const [pattern, reason] of DENIED_ESCALATIONS) {
  if (pattern.test(command)) {
    denyPermissionRequest(reason);
    process.exit(0);
  }
}
