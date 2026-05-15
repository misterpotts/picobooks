package dev.mjkpotts.tinyledger.domain;

import java.time.Instant;
import java.util.UUID;

public record LedgerEntry(
        UUID transactionId,
        AccountId accountId,
        MovementType type,
        Money amount,
        Money resultingBalance,
        String reference,
        Instant occurredAt
) {
}
