package dev.mjkpotts.picobooks.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

record MoneyAmountRequest(
        @Positive long value,
        @NotBlank String currency
) {
}
