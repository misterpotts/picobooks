package dev.mjkpotts.picobooks.domain;

import java.util.UUID;

/**
 * Value object for a producer-generated UUID v7 transaction identifier.
 */
public record TransactionId(UUID value) {

    public TransactionId {
        if (value == null || value.version() != 7) {
            throw new InvalidTransactionIdException();
        }
    }

    public String asString() {
        return value.toString();
    }
}
