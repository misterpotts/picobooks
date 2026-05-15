package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.Money;
import dev.mjkpotts.picobooks.domain.TransactionType;

public record RecordTransactionInput(
        TransactionType type,
        Money amount,
        String reference
) {
}
