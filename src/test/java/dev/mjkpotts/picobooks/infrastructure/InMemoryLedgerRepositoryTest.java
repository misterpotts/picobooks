package dev.mjkpotts.picobooks.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.AccountLedger;
import dev.mjkpotts.picobooks.domain.Balance;
import dev.mjkpotts.picobooks.domain.Money;
import dev.mjkpotts.picobooks.domain.TransactionId;
import dev.mjkpotts.picobooks.domain.TransactionType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class InMemoryLedgerRepositoryTest {

    private final InMemoryLedgerRepository repository = new InMemoryLedgerRepository();

    @Test
    void independentAccountPartitionsDoNotInterfere() {
        var first = new AccountId(uuidV7(1));
        var second = new AccountId(uuidV7(2));
        repository.create(AccountLedger.create(first, "GBP", Instant.parse("2026-05-15T12:00:00Z")));
        repository.create(AccountLedger.create(second, "EUR", Instant.parse("2026-05-15T12:00:00Z")));

        repository.update(first, ledger -> ledger.record(TransactionType.DEPOSIT, new Money(100, "GBP"), "", new TransactionId(uuidV7(3)), Instant.parse("2026-05-15T12:01:00Z")));
        repository.update(second, ledger -> ledger.record(TransactionType.DEPOSIT, new Money(50, "EUR"), "", new TransactionId(uuidV7(4)), Instant.parse("2026-05-15T12:01:00Z")));

        assertEquals(new Balance(100, "GBP"), repository.findById(first).orElseThrow().currentBalance());
        assertEquals(new Balance(50, "EUR"), repository.findById(second).orElseThrow().currentBalance());
    }

    @Test
    void appendOrderingIsPreserved() {
        var accountId = new AccountId(uuidV7(1));
        repository.create(AccountLedger.create(accountId, "GBP", Instant.parse("2026-05-15T12:00:00Z")));

        repository.update(accountId, ledger -> ledger.record(TransactionType.DEPOSIT, new Money(10, "GBP"), "one", new TransactionId(uuidV7(2)), Instant.parse("2026-05-15T12:01:00Z")));
        repository.update(accountId, ledger -> ledger.record(TransactionType.DEPOSIT, new Money(20, "GBP"), "two", new TransactionId(uuidV7(3)), Instant.parse("2026-05-15T12:02:00Z")));

        var entries = repository.findById(accountId).orElseThrow().entries();
        assertEquals("one", entries.get(0).reference());
        assertEquals("two", entries.get(1).reference());
    }

    @Test
    void concurrentWritesToOneAccountObserveLinearizedState() throws Exception {
        var accountId = new AccountId(uuidV7(1));
        var writes = 40;
        repository.create(AccountLedger.create(accountId, "GBP", Instant.parse("2026-05-15T12:00:00Z")));
        var start = new CountDownLatch(1);
        var failures = new ArrayList<Throwable>();

        try (var executor = Executors.newFixedThreadPool(8)) {
            for (int i = 0; i < writes; i++) {
                var seed = i + 2L;
                executor.submit(() -> {
                    try {
                        start.await();
                        repository.update(accountId, ledger -> ledger.record(
                                TransactionType.DEPOSIT,
                                new Money(1, "GBP"),
                                "",
                                new TransactionId(uuidV7(seed)),
                                Instant.parse("2026-05-15T12:01:00Z")
                        ));
                    } catch (Throwable throwable) {
                        synchronized (failures) {
                            failures.add(throwable);
                        }
                    }
                });
            }
            start.countDown();
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        assertEquals(0, failures.size());
        var ledger = repository.findById(accountId).orElseThrow();
        assertEquals(new Balance(writes, "GBP"), ledger.currentBalance());
        assertEquals(writes, ledger.entries().size());
    }

    private static UUID uuidV7(long seed) {
        var timestamp = 1_765_000_000_000L + seed;
        var mostSignificantBits = (timestamp << 16) | 0x7000L | (seed & 0xfffL);
        var leastSignificantBits = 0x8000000000000000L | (seed & 0x3fffffffffffffffL);
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
