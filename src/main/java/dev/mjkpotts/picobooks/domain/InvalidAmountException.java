package dev.mjkpotts.picobooks.domain;

/**
 * Raised when a money or balance amount violates ledger value rules.
 */
public final class InvalidAmountException extends LedgerException {

    private static final String ERROR_CODE = "invalid_amount";

    public InvalidAmountException(String message) {
        super(ERROR_CODE, message);
    }
}
