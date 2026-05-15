package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountId;
import java.time.Instant;

public record CreatedAccount(AccountId accountId, Instant createdAt) {
}
