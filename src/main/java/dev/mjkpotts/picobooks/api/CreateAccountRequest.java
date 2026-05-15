package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.InvalidCurrencyException;
import java.util.Locale;

/**
 * Request body for explicit account creation.
 */
record CreateAccountRequest(String currency) {

    CreateAccountRequest {
        if (currency == null || currency.isBlank()) {
            throw new InvalidCurrencyException("Currency is required");
        }
        currency = currency.trim().toUpperCase(Locale.ROOT);
        if (currency.length() != 3 || !currency.chars().allMatch(Character::isLetter)) {
            throw new InvalidCurrencyException("Currency must be a three-letter code");
        }
    }
}
