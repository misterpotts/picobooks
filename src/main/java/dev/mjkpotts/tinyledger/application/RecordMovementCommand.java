package dev.mjkpotts.tinyledger.application;

import dev.mjkpotts.tinyledger.domain.Money;
import dev.mjkpotts.tinyledger.domain.MovementType;

public record RecordMovementCommand(
        MovementType type,
        Money amount,
        String reference
) {
}
