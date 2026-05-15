package dev.mjkpotts.picobooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void normalizesCurrency() {
        var money = new Money(100, " gbp ");

        assertEquals(100, money.value());
        assertEquals("GBP", money.currency());
    }

    @Test
    void rejectsNonPositiveAmount() {
        var exception = assertThrows(LedgerException.class, () -> new Money(0, "GBP"));

        assertEquals(LedgerErrorCode.INVALID_AMOUNT, exception.code());
        assertEquals("Amount value must be positive", exception.getMessage());
    }

    @Test
    void rejectsBlankCurrency() {
        var exception = assertThrows(LedgerException.class, () -> new Money(100, " "));

        assertEquals(LedgerErrorCode.INVALID_CURRENCY, exception.code());
        assertEquals("Currency is required", exception.getMessage());
    }

    @Test
    void rejectsInvalidCurrencyCode() {
        var exception = assertThrows(LedgerException.class, () -> new Money(100, "GB"));

        assertEquals(LedgerErrorCode.INVALID_CURRENCY, exception.code());
        assertEquals("Currency must be a three-letter code", exception.getMessage());
    }
}
