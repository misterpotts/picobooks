package dev.mjkpotts.tinyledger.api;

import dev.mjkpotts.tinyledger.application.RecordMovementCommand;
import dev.mjkpotts.tinyledger.domain.Money;
import dev.mjkpotts.tinyledger.domain.MovementType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record RecordMovementRequest(
        @NotNull MovementType type,
        @Valid @NotNull MoneyAmountRequest amount,
        String reference
) {

    RecordMovementCommand toCommand() {
        return new RecordMovementCommand(type, new Money(amount.amountMinor(), amount.currency()), reference);
    }
}
