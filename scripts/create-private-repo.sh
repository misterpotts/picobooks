#!/usr/bin/env bash
set -euo pipefail

OWNER="${1:-misterpotts}"
REPO="${2:-teya-tiny-ledger-codex-harness}"

if ! command -v gh >/dev/null 2>&1; then
  echo "GitHub CLI 'gh' is required to create and push a private repository." >&2
  exit 1
fi

if [ ! -d .git ]; then
  git init
  git branch -M main
fi

git add .
git commit -m "Create Codex-governed tiny ledger skeleton" || true

gh repo create "${OWNER}/${REPO}" --private --source . --push
