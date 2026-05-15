package dev.mjkpotts.picobooks.domain;

import java.util.UUID;

/**
 * Value object for a service-generated UUID v7 account identifier.
 */
public record AccountId(UUID value) {

    public AccountId {
        if (value == null || value.version() != 7) {
            throw new InvalidAccountIdException();
        }
    }

    public AccountId(String value) {
        this(parse(value));
    }

    public String asString() {
        return value.toString();
    }

    private static UUID parse(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new InvalidAccountIdException();
        }
    }
}
