package dev.mjkpotts.picobooks.domain;

public record AccountId(String value) {

    public AccountId {
        if (value == null || value.isBlank()) {
            throw new InvalidDomainRequestException("accountId must not be blank");
        }
    }
}
