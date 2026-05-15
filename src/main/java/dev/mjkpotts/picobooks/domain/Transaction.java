package dev.mjkpotts.picobooks.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable accepted ledger transaction with its resulting account balance.
 */
public record Transaction(
        TransactionId transactionId,
        AccountId accountId,
        TransactionType type,
        Money amount,
        Balance resultingBalance,
        String reference,
        Instant occurredAt
) {
    public Transaction {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(accountId, "accountId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(resultingBalance, "resultingBalance must not be null");
        reference = reference == null ? "" : reference;
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }
}
