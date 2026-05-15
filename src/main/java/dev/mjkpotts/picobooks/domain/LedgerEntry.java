package dev.mjkpotts.picobooks.domain;

import java.time.Instant;
import java.util.UUID;

public record LedgerEntry(
        UUID transactionId,
        AccountId accountId,
        TransactionType type,
        Money amount,
        Money resultingBalance,
        String reference,
        Instant occurredAt
) {
}
