package dev.mjkpotts.picobooks.domain;

/**
 * Raised when a withdrawal would make an account ledger balance negative.
 */
public final class InsufficientFundsException extends LedgerException {

    private static final String ERROR_CODE = "insufficient_funds";

    public InsufficientFundsException() {
        super(ERROR_CODE, "Withdrawal would overdraw account");
    }
}
