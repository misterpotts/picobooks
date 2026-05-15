package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.Money;
import dev.mjkpotts.picobooks.domain.TransactionType;

/**
 * Application command for recording one ledger transaction.
 */
public record RecordTransactionInput(
        TransactionType type,
        Money amount,
        String reference
) {
}
