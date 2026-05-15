package dev.mjkpotts.picobooks.domain;

/**
 * Raised when a transaction currency differs from the account ledger currency.
 */
public final class CurrencyMismatchException extends LedgerException {

    private static final String ERROR_CODE = "currency_mismatch";

    public CurrencyMismatchException() {
        super(ERROR_CODE, "Transaction currency must match account currency");
    }
}
