import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { join, resolve } from "node:path";
import { test } from "node:test";

const repoRoot = resolve(import.meta.dirname, "..", "..");
const specPath = join(repoRoot, "openspec", "specs", "picobooks", "spec.md");

function specText() {
  return readFileSync(specPath, "utf8").replace(/\r\n/g, "\n");
}

test("picobooks spec defines the concrete HTTP API contract", () => {
  const text = specText();

  assert.match(text, /## API Contract/);
  assert.match(text, /`GET \/health`/);
  assert.match(text, /`POST \/accounts\/\{accountId\}\/transactions`/);
  assert.match(text, /`GET \/accounts\/\{accountId\}\/balance`/);
  assert.match(text, /`GET \/accounts\/\{accountId\}\/transactions`/);
  assert.match(text, /"status": "UP"/);
  assert.match(text, /"transactionId"/);
  assert.match(text, /"accountId"/);
  assert.match(text, /"amountMinor"/);
  assert.match(text, /"currency"/);
  assert.match(text, /"resultingBalance"/);
  assert.match(text, /"occurredAt"/);
  assert.match(text, /"code": "invalid_ledger_request"/);
});

test("picobooks spec pins core ledger behavior", () => {
  const text = specText();

  assert.match(text, /no ledger entry is recorded/);
  assert.match(text, /single currency per account/i);
  assert.match(text, /sufficient funds/);
  assert.match(text, /append order/);
  assert.match(text, /Non-positive amounts are rejected/);
  assert.match(text, /Floating point money values SHALL NOT be accepted/);
  assert.match(text, /currency becomes the account currency/);
});

test("picobooks spec defines domain objects and account aggregate", () => {
  const text = specText();

  assert.match(text, /## Domain Object Definitions/);
  assert.match(text, /\*\*AccountId\*\*/);
  assert.match(text, /\*\*Money\*\*/);
  assert.match(text, /\*\*Balance\*\*/);
  assert.match(text, /\*\*LedgerEntry\*\*/);
  assert.match(text, /\*\*AccountLedger\*\*/);
  assert.match(text, /Aggregate for exactly one account/);
  assert.match(text, /currency consistency/);
  assert.match(text, /sufficient-funds checks/);
});

test("picobooks spec keeps contention account-scoped with no global ledger", () => {
  const text = specText();

  assert.match(text, /## Solution Space/);
  assert.match(text, /### Object Graph/);
  assert.match(text, /### In-Memory Data Model/);
  assert.match(text, /### Concurrency And Contention Decision/);
  assert.match(text, /partitioned by `AccountId`/);
  assert.match(text, /No domain object named or behaving as a global ledger/);
  assert.match(text, /No global\s+ledger aggregate/);
  assert.match(text, /Contention SHALL be possible only at the account level/);
  assert.match(text, /different accounts\s+MUST NOT share/);
  assert.match(text, /per-account synchronization strategy/);
  assert.match(text, /does not provide cross-account transactions/);
});
