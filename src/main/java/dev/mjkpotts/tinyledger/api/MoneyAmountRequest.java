package dev.mjkpotts.tinyledger.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

record MoneyAmountRequest(
        @Positive long amountMinor,
        @NotBlank String currency
) {
}
