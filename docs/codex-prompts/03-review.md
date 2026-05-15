Review the current diff skeptically.

Assess against:
- `openspec/specs/tiny-ledger/spec.md`;
- the active OpenSpec delta, if present locally;
- README examples;
- AGENTS.md constraints;
- Conventional Commit messages and PR title expectations;
- assessment simplicity.

Look for:
- money model mistakes;
- floating point usage;
- missing validation;
- overdraw bugs;
- currency mismatch bugs;
- mutable state hazards;
- insufficient tests;
- accidental WebFlux/reactive code;
- over-engineering;
- stale README or spec.

Return findings ranked by severity and suggest the smallest corrective changes.
