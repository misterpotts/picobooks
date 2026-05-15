package dev.mjkpotts.picobooks.domain;

import java.util.UUID;

public record TransactionId(UUID value) {

    public TransactionId {
        if (value == null || value.version() != 7) {
            throw new IllegalArgumentException("transactionId must be a UUID v7 value");
        }
    }

    public String asString() {
        return value.toString();
    }
}
