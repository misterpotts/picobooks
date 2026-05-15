package dev.mjkpotts.picobooks.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AccountLedger {

    private final AccountId accountId;
    private final String currency;
    private final Instant createdAt;
    private final List<Transaction> entries = new ArrayList<>();
    private Balance currentBalance;

    private AccountLedger(AccountId accountId, String currency, Instant createdAt) {
        this.accountId = Objects.requireNonNull(accountId, "accountId must not be null");
        this.currentBalance = new Balance(0, currency);
        this.currency = this.currentBalance.currency();
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static AccountLedger create(AccountId accountId, String currency, Instant createdAt) {
        return new AccountLedger(accountId, currency, createdAt);
    }

    public AccountId accountId() {
        return accountId;
    }

    public String currency() {
        return currency;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public synchronized Balance currentBalance() {
        return currentBalance;
    }

    public synchronized List<Transaction> entries() {
        return List.copyOf(entries);
    }

    public synchronized Transaction record(
            TransactionType type,
            Money amount,
            String reference,
            TransactionId transactionId,
            Instant occurredAt
    ) {
        if (amount == null) {
            throw new LedgerException(LedgerErrorCode.INVALID_AMOUNT, "Transaction amount is required");
        }
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (type == null) {
            throw new LedgerException(LedgerErrorCode.INVALID_TRANSACTION_TYPE, "Transaction type is required");
        }
        if (!currency.equals(amount.currency())) {
            throw new LedgerException(LedgerErrorCode.CURRENCY_MISMATCH, "Transaction currency must match account currency");
        }

        var resultingBalance = switch (type) {
            case DEPOSIT -> new Balance(currentBalance.value() + amount.value(), currency);
            case WITHDRAWAL -> withdraw(amount);
        };
        var transaction = new Transaction(
                transactionId,
                accountId,
                type,
                amount,
                resultingBalance,
                reference == null ? "" : reference,
                occurredAt
        );
        currentBalance = resultingBalance;
        entries.add(transaction);
        return transaction;
    }

    private Balance withdraw(Money amount) {
        var resultingValue = currentBalance.value() - amount.value();
        if (resultingValue < 0) {
            throw new LedgerException(LedgerErrorCode.INSUFFICIENT_FUNDS, "Withdrawal would overdraw account");
        }
        return new Balance(resultingValue, currency);
    }
}
