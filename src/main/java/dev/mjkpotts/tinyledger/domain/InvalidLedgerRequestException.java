package dev.mjkpotts.tinyledger.domain;

public final class InvalidLedgerRequestException extends RuntimeException {

    public InvalidLedgerRequestException(String message) {
        super(message);
    }
}
