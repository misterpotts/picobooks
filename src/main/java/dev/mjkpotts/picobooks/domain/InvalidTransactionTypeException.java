package dev.mjkpotts.picobooks.domain;

/**
 * Raised when a transaction type is missing or not supported by the ledger.
 */
public final class InvalidTransactionTypeException extends LedgerException {

    private static final String ERROR_CODE = "invalid_transaction_type";

    public InvalidTransactionTypeException(String message) {
        super(ERROR_CODE, message);
    }
}
