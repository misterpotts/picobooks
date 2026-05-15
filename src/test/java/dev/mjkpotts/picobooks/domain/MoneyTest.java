package dev.mjkpotts.picobooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

        assertInstanceOf(InvalidAmountException.class, exception);
        assertEquals("invalid_amount", exception.wireCode());
        assertEquals("Amount value must be positive", exception.getMessage());
    }

    @Test
    void rejectsBlankCurrency() {
        var exception = assertThrows(LedgerException.class, () -> new Money(100, " "));

        assertInstanceOf(InvalidCurrencyException.class, exception);
        assertEquals("invalid_currency", exception.wireCode());
        assertEquals("Currency is required", exception.getMessage());
    }

    @Test
    void rejectsInvalidCurrencyCode() {
        var exception = assertThrows(LedgerException.class, () -> new Money(100, "GB"));

        assertInstanceOf(InvalidCurrencyException.class, exception);
        assertEquals("invalid_currency", exception.wireCode());
        assertEquals("Currency must be a three-letter code", exception.getMessage());
    }
}
