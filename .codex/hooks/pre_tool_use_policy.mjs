#!/usr/bin/env node
import {
  commandSegments,
  commandFromEvent,
  denyPreToolUse,
  firstOptionValue,
  hasTrackedOpenSpecChanges,
  isConventionalCommitTitle,
  quotedFlagValues,
  readEvent,
  tokenizeCommandSegment,
} from "./hook_utils.mjs";

const BLOCKED_PATTERNS = [
  [/\brm\s+-rf\s+(\/|\.|\.\.|\*)\b/, "Refusing broad destructive delete."],
  [/\bRemove-Item\b(?=.*\b-Recurse\b)(?=.*\b-Force\b).*(^|\s)(\.|\*|\/|[A-Z]:\\)\s*$/i, "Refusing broad destructive delete."],
  [/\bgit\s+push\s+--force\b/, "Refusing force push."],
  [/\bgit\s+push\b(?=.*\borigin\b)(?=.*\bmain\b)/, "Refusing direct push to main. Open or update a PR branch for human review."],
  [/\bgit\s+reset\s+--hard\b/, "Refusing destructive git reset."],
  [/\bgit\s+checkout\s+--\s+(\.|\*)\b/, "Refusing broad checkout that would discard local work."],
  [/\bgh\s+pr\s+merge\b/, "Refusing PR merge. Human review and merge are required."],
  [/\bgh\s+pr\s+create\b(?=.*\s--draft\b)/, "Refusing draft PR creation. Agents must open non-draft PRs for human review."],
  [/\bgh\s+pr\s+ready\b(?=.*\s--undo\b)/, "Refusing to convert a PR back to draft. Agents must keep PRs reviewable."],
  [/\bchmod\s+-R\s+777\b/, "Refusing broad world-writable permission change."],
  [/\b(curl|wget)\b.+\|\s*(sh|bash|pwsh|powershell)\b/i, "Refusing pipe-to-shell network execution."],
  [/\b(iwr|irm|Invoke-WebRequest|Invoke-RestMethod)\b.+\|\s*(iex|Invoke-Expression|pwsh|powershell)\b/i, "Refusing pipe-to-shell network execution."],
  [/\b(apt|apt-get|brew|choco|winget)\s+install\b/i, "Installing system packages is outside the assessment workflow."],
];

function stagesOpenSpecChanges(command, cwd) {
  if (!/\bgit\s+add\b/.test(command)) {
    return false;
  }
  if (/openspec[\\/]+changes/.test(command)) {
    return true;
  }
  return /\bgit\s+add\s+(-A|--all|\.|-u|--update)\b/.test(command) && hasTrackedOpenSpecChanges(cwd);
}

function invalidCommitMessage(command) {
  for (const segment of commandSegments(command)) {
    const tokens = tokenizeCommandSegment(segment);
    if (tokens[0] !== "git" || tokens[1] !== "commit") {
      continue;
    }
    const message = firstOptionValue(tokens, "-m") || firstOptionValue(tokens, "--message");
    if (message && !isConventionalCommitTitle(message)) {
      return message;
    }
  }
  return "";
}

function invalidPrTitle(command) {
  if (!/\bgh\s+pr\s+(create|edit)\b/.test(command)) {
    return "";
  }
  const titles = quotedFlagValues(command, "--title");
  return titles.find((title) => title && !isConventionalCommitTitle(title)) ?? "";
}

const event = await readEvent();
const command = commandFromEvent(event);
const cwd = String(event.cwd ?? process.cwd());

if (command) {
  for (const [pattern, reason] of BLOCKED_PATTERNS) {
    if (pattern.test(command)) {
      denyPreToolUse(reason);
      process.exit(0);
    }
  }

  if (stagesOpenSpecChanges(command, cwd)) {
    denyPreToolUse("Active OpenSpec deltas under openspec/changes/** must not be staged. Copy the delta into the PR body and apply accepted behaviour under openspec/specs/**.");
    process.exit(0);
  }

  const badCommitMessage = invalidCommitMessage(command);
  if (badCommitMessage) {
    denyPreToolUse(`Commit message must use Conventional Commits and keep bodies short. Invalid subject: ${badCommitMessage}`);
    process.exit(0);
  }

  const badPrTitle = invalidPrTitle(command);
  if (badPrTitle) {
    denyPreToolUse(`PR title must pass Conventional Commit linting. Invalid title: ${badPrTitle}`);
    process.exit(0);
  }
}
