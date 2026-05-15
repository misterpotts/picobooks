package dev.mjkpotts.picobooks.domain;

import java.util.Locale;

public record Balance(long value, String currency) {

    public Balance {
        if (value < 0) {
            throw new LedgerException(LedgerErrorCode.INVALID_AMOUNT, "Balance value must not be negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new LedgerException(LedgerErrorCode.INVALID_CURRENCY, "Currency is required");
        }
        currency = currency.trim().toUpperCase(Locale.ROOT);
        if (!currency.matches("[A-Z]{3}")) {
            throw new LedgerException(LedgerErrorCode.INVALID_CURRENCY, "Currency must be a three-letter code");
        }
    }
}
