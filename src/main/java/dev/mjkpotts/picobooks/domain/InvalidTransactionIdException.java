package dev.mjkpotts.picobooks.domain;

/**
 * Raised when a transaction identifier is missing or not a UUID v7 value.
 */
public final class InvalidTransactionIdException extends LedgerException {

    private static final String ERROR_CODE = "invalid_transaction_id";

    public InvalidTransactionIdException() {
        super(ERROR_CODE, "transactionId must be a UUID v7 value");
    }
}
