package dev.mjkpotts.picobooks.domain;

public final class LedgerException extends RuntimeException {

    private final LedgerErrorCode code;

    public LedgerException(LedgerErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public LedgerErrorCode code() {
        return code;
    }
}
