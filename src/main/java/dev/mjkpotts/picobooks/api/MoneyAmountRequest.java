package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.LedgerErrorCode;
import dev.mjkpotts.picobooks.domain.LedgerException;
import dev.mjkpotts.picobooks.domain.Money;
import java.util.Locale;

record MoneyAmountRequest(
        long value,
        String currency
) {

    MoneyAmountRequest {
        if (value <= 0) {
            throw new LedgerException(LedgerErrorCode.INVALID_AMOUNT, "Amount value must be positive");
        }
        if (currency == null || currency.isBlank()) {
            throw new LedgerException(LedgerErrorCode.INVALID_CURRENCY, "Currency is required");
        }
        currency = currency.trim().toUpperCase(Locale.ROOT);
        if (currency.length() != 3 || !currency.chars().allMatch(Character::isLetter)) {
            throw new LedgerException(LedgerErrorCode.INVALID_CURRENCY, "Currency must be a three-letter code");
        }
    }

    Money toMoney() {
        return new Money(value, currency);
    }
}
