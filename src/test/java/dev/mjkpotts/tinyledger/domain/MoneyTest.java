package dev.mjkpotts.tinyledger.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void normalizesCurrency() {
        var money = new Money(100, " gbp ");

        assertEquals(100, money.amountMinor());
        assertEquals("GBP", money.currency());
    }

    @Test
    void rejectsNonPositiveAmount() {
        var exception = assertThrows(InvalidLedgerRequestException.class, () -> new Money(0, "GBP"));

        assertEquals("amountMinor must be positive", exception.getMessage());
    }

    @Test
    void rejectsBlankCurrency() {
        var exception = assertThrows(InvalidLedgerRequestException.class, () -> new Money(100, " "));

        assertEquals("currency must not be blank", exception.getMessage());
    }

    @Test
    void rejectsInvalidCurrencyCode() {
        var exception = assertThrows(InvalidLedgerRequestException.class, () -> new Money(100, "GB"));

        assertEquals("currency must be an ISO-style three-letter code", exception.getMessage());
    }
}
