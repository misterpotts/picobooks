#!/usr/bin/env bash
set -euo pipefail

mvn test
node --test tests/hooks/codex-hooks.test.mjs tests/skills/skill-config.test.mjs
node .codex/hooks/check_repository_constraints.mjs

if git ls-files openspec/changes | grep -q .; then
  echo "Active OpenSpec deltas must not be committed: openspec/changes/**" >&2
  exit 1
fi

echo "Quality gates passed."
