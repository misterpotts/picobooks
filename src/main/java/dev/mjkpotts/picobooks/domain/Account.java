package dev.mjkpotts.picobooks.domain;

import java.util.Objects;

public record Account(AccountId accountId, Ledger ledger) {

    public Account {
        Objects.requireNonNull(accountId, "accountId must not be null");
        Objects.requireNonNull(ledger, "ledger must not be null");
    }
}
