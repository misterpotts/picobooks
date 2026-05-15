package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountId;
import java.time.Instant;

/**
 * Application result for a newly created account ledger.
 */
public record CreatedAccount(AccountId accountId, Instant createdAt) {
}
