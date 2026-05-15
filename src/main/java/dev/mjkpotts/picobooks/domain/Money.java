package dev.mjkpotts.picobooks.domain;

import java.util.Locale;

public record Money(long amountMinor, String currency) {

    public Money {
        if (amountMinor <= 0) {
            throw new InvalidLedgerRequestException("amountMinor must be positive");
        }
        if (currency == null || currency.isBlank()) {
            throw new InvalidLedgerRequestException("currency must not be blank");
        }
        currency = currency.trim().toUpperCase(Locale.ROOT);
        if (!currency.matches("[A-Z]{3}")) {
            throw new InvalidLedgerRequestException("currency must be an ISO-style three-letter code");
        }
    }
}
