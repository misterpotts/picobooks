package dev.mjkpotts.picobooks.domain;

/**
 * Base type for ledger-domain failures that are returned to API clients with stable wire codes.
 */
public abstract class LedgerException extends RuntimeException {

    private final String wireCode;

    protected LedgerException(String wireCode, String message) {
        super(message);
        this.wireCode = wireCode;
    }

    public final String wireCode() {
        return wireCode;
    }
}
