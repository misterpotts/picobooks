package dev.mjkpotts.picobooks.domain;

/**
 * Raised when a currency is missing or is not a three-letter code.
 */
public final class InvalidCurrencyException extends LedgerException {

    private static final String ERROR_CODE = "invalid_currency";

    public InvalidCurrencyException(String message) {
        super(ERROR_CODE, message);
    }
}
