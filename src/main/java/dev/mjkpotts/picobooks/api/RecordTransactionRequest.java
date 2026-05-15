package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.application.RecordTransactionInput;
import dev.mjkpotts.picobooks.domain.Money;
import dev.mjkpotts.picobooks.domain.TransactionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record RecordTransactionRequest(
        @NotNull TransactionType type,
        @Valid @NotNull MoneyAmountRequest amount,
        String reference
) {

    RecordTransactionInput toCommand() {
        return new RecordTransactionInput(type, new Money(amount.value(), amount.currency()), reference);
    }
}
