package dev.mjkpotts.picobooks.domain;

import java.util.Locale;

/**
 * Value object for a non-negative account balance in currency base units.
 */
public record Balance(long value, String currency) {

    public Balance {
        if (value < 0) {
            throw new InvalidAmountException("Balance value must not be negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new InvalidCurrencyException("Currency is required");
        }
        currency = currency.trim().toUpperCase(Locale.ROOT);
        if (!currency.matches("[A-Z]{3}")) {
            throw new InvalidCurrencyException("Currency must be a three-letter code");
        }
    }
}
