#!/usr/bin/env bash
set -euo pipefail

git config core.hooksPath .githooks
chmod +x .githooks/commit-msg .githooks/pre-push 2>/dev/null || true
echo "Configured git to use repository hooks from .githooks."
