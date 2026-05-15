package dev.mjkpotts.picobooks.domain;

import java.util.UUID;

public record AccountId(UUID value) {

    public AccountId {
        if (value == null || value.version() != 7) {
            throw new LedgerException(LedgerErrorCode.INVALID_ACCOUNT_ID, "accountId must be a UUID v7 value");
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
            throw new LedgerException(LedgerErrorCode.INVALID_ACCOUNT_ID, "accountId must be a UUID v7 value");
        }
    }
}
