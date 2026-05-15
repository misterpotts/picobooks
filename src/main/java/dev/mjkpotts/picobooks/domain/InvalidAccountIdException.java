package dev.mjkpotts.picobooks.domain;

/**
 * Raised when an account identifier is missing, malformed, or not a UUID v7 value.
 */
public final class InvalidAccountIdException extends LedgerException {

    private static final String ERROR_CODE = "invalid_account_id";

    public InvalidAccountIdException() {
        super(ERROR_CODE, "accountId must be a UUID v7 value");
    }
}
