package dev.mjkpotts.picobooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountLedgerTest {

    private final AccountId accountId = new AccountId(uuidV7(1));

    @Test
    void depositIncreasesBalanceAndAppendsTransaction() {
        var ledger = AccountLedger.create(accountId, "gbp", Instant.parse("2026-05-15T12:00:00Z"));

        var transaction = ledger.record(
                TransactionType.DEPOSIT,
                new Money(100, "GBP"),
                "Initial deposit",
                new TransactionId(uuidV7(2)),
                Instant.parse("2026-05-15T12:01:00Z")
        );

        assertEquals(new Balance(100, "GBP"), ledger.currentBalance());
        assertEquals(transaction, ledger.entries().getFirst());
        assertEquals(new Balance(100, "GBP"), transaction.resultingBalance());
    }

    @Test
    void withdrawalDecreasesBalance() {
        var ledger = AccountLedger.create(accountId, "GBP", Instant.parse("2026-05-15T12:00:00Z"));
        ledger.record(TransactionType.DEPOSIT, new Money(100, "GBP"), "", new TransactionId(uuidV7(2)), Instant.parse("2026-05-15T12:01:00Z"));

        ledger.record(TransactionType.WITHDRAWAL, new Money(40, "GBP"), "", new TransactionId(uuidV7(3)), Instant.parse("2026-05-15T12:02:00Z"));

        assertEquals(new Balance(60, "GBP"), ledger.currentBalance());
        assertEquals(2, ledger.entries().size());
    }

    @Test
    void overdrawIsRejectedWithoutAppend() {
        var ledger = AccountLedger.create(accountId, "GBP", Instant.parse("2026-05-15T12:00:00Z"));

        var exception = assertThrows(LedgerException.class, () -> ledger.record(
                TransactionType.WITHDRAWAL,
                new Money(1, "GBP"),
                "",
                new TransactionId(uuidV7(2)),
                Instant.parse("2026-05-15T12:01:00Z")
        ));

        assertEquals(LedgerErrorCode.INSUFFICIENT_FUNDS, exception.code());
        assertEquals(new Balance(0, "GBP"), ledger.currentBalance());
        assertEquals(0, ledger.entries().size());
    }

    @Test
    void currencyMismatchIsRejectedWithoutAppend() {
        var ledger = AccountLedger.create(accountId, "GBP", Instant.parse("2026-05-15T12:00:00Z"));

        var exception = assertThrows(LedgerException.class, () -> ledger.record(
                TransactionType.DEPOSIT,
                new Money(100, "EUR"),
                "",
                new TransactionId(uuidV7(2)),
                Instant.parse("2026-05-15T12:01:00Z")
        ));

        assertEquals(LedgerErrorCode.CURRENCY_MISMATCH, exception.code());
        assertEquals(new Balance(0, "GBP"), ledger.currentBalance());
        assertEquals(0, ledger.entries().size());
    }

    @Test
    void historyPreservesAppendOrder() {
        var ledger = AccountLedger.create(accountId, "GBP", Instant.parse("2026-05-15T12:00:00Z"));
        var first = ledger.record(TransactionType.DEPOSIT, new Money(100, "GBP"), "one", new TransactionId(uuidV7(2)), Instant.parse("2026-05-15T12:01:00Z"));
        var second = ledger.record(TransactionType.DEPOSIT, new Money(50, "GBP"), "two", new TransactionId(uuidV7(3)), Instant.parse("2026-05-15T12:02:00Z"));

        assertEquals(first, ledger.entries().get(0));
        assertEquals(second, ledger.entries().get(1));
    }

    private static UUID uuidV7(long seed) {
        var timestamp = 1_765_000_000_000L + seed;
        var mostSignificantBits = (timestamp << 16) | 0x7000L | (seed & 0xfffL);
        var leastSignificantBits = 0x8000000000000000L | (seed & 0x3fffffffffffffffL);
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
