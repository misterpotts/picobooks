package dev.mjkpotts.picobooks.domain;

public final class InvalidLedgerRequestException extends RuntimeException {

    public InvalidLedgerRequestException(String message) {
        super(message);
    }
}
