package dev.mjkpotts.picobooks.domain;

import java.util.List;
import java.util.Objects;

public record Ledger(List<Transaction> transactions, Balance balance) {

    public Ledger {
        transactions = List.copyOf(Objects.requireNonNull(transactions, "transactions must not be null"));
        Objects.requireNonNull(balance, "balance must not be null");
    }
}
