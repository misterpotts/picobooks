package dev.mjkpotts.tinyledger.application;

import dev.mjkpotts.tinyledger.domain.Money;
import dev.mjkpotts.tinyledger.domain.TransactionType;

public record RecordTransactionInput(
        TransactionType type,
        Money amount,
        String reference
) {
}
