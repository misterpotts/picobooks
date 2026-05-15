package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.InvalidAmountException;
import dev.mjkpotts.picobooks.domain.InvalidCurrencyException;
import dev.mjkpotts.picobooks.domain.Money;
import java.util.Locale;

/**
 * Request representation of a positive money amount in currency base units.
 */
record MoneyAmountRequest(
        long value,
        String currency
) {

    MoneyAmountRequest {
        if (value <= 0) {
            throw new InvalidAmountException("Amount value must be positive");
        }
        if (currency == null || currency.isBlank()) {
            throw new InvalidCurrencyException("Currency is required");
        }
        currency = currency.trim().toUpperCase(Locale.ROOT);
        if (currency.length() != 3 || !currency.chars().allMatch(Character::isLetter)) {
            throw new InvalidCurrencyException("Currency must be a three-letter code");
        }
    }

    Money toMoney() {
        return new Money(value, currency);
    }
}
