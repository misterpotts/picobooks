package dev.mjkpotts.picobooks.domain;

import java.util.Locale;

public record Balance(long value, String currency) {

    public Balance {
        if (value < 0) {
            throw new InvalidDomainRequestException("balance value must not be negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new InvalidDomainRequestException("currency must not be blank");
        }
        currency = currency.trim().toUpperCase(Locale.ROOT);
        if (!currency.matches("[A-Z]{3}")) {
            throw new InvalidDomainRequestException("currency must be an ISO-style three-letter code");
        }
    }
}
