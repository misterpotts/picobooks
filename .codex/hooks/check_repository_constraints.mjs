#!/usr/bin/env node
import { repositoryConstraintViolations } from "./hook_utils.mjs";

const violations = repositoryConstraintViolations(process.cwd());

if (violations.length > 0) {
  process.stderr.write(`Repository constraints are unmet:\n- ${violations.join("\n- ")}\n`);
  process.exit(1);
}
