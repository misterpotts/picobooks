package dev.mjkpotts.picobooks.domain;

import java.time.Instant;
import java.util.UUID;

public record Transaction(
        UUID transactionId,
        AccountId accountId,
        TransactionType type,
        Money amount,
        Balance resultingBalance,
        String reference,
        Instant occurredAt
) {
}
