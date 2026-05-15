package dev.mjkpotts.picobooks.domain;

import java.util.Locale;

/**
 * Value object for a positive money amount in currency base units.
 */
public record Money(long value, String currency) {

    public Money {
        if (value <= 0) {
            throw new InvalidAmountException("Amount value must be positive");
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
