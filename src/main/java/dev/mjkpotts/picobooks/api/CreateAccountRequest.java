package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.LedgerErrorCode;
import dev.mjkpotts.picobooks.domain.LedgerException;
import java.util.Locale;

record CreateAccountRequest(String currency) {

    CreateAccountRequest {
        if (currency == null || currency.isBlank()) {
            throw new LedgerException(LedgerErrorCode.INVALID_CURRENCY, "Currency is required");
        }
        currency = currency.trim().toUpperCase(Locale.ROOT);
        if (currency.length() != 3 || !currency.chars().allMatch(Character::isLetter)) {
            throw new LedgerException(LedgerErrorCode.INVALID_CURRENCY, "Currency must be a three-letter code");
        }
    }
}
