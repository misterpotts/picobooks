package dev.mjkpotts.tinyledger.api;

import dev.mjkpotts.tinyledger.application.RecordTransactionCommand;
import dev.mjkpotts.tinyledger.domain.Money;
import dev.mjkpotts.tinyledger.domain.TransactionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record RecordTransactionRequest(
        @NotNull TransactionType type,
        @Valid @NotNull MoneyAmountRequest amount,
        String reference
) {

    RecordTransactionCommand toCommand() {
        return new RecordTransactionCommand(type, new Money(amount.amountMinor(), amount.currency()), reference);
    }
}
