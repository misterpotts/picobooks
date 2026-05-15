package dev.mjkpotts.picobooks.infrastructure;

import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.LedgerEntry;
import dev.mjkpotts.picobooks.domain.Money;
import dev.mjkpotts.picobooks.domain.TransactionType;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryLedgerRepositoryTest {

    private final InMemoryLedgerRepository repository = new InMemoryLedgerRepository();
    private final AccountId accountId = new AccountId("merchant-123");

    @Test
    void findByAccountIdRemainsSkeleton() {
        assertThrows(UnsupportedOperationException.class, () -> repository.findByAccountId(accountId));
    }

    @Test
    void appendRemainsSkeleton() {
        var entry = new LedgerEntry(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                accountId,
                TransactionType.DEPOSIT,
                new Money(100, "GBP"),
                new Money(100, "GBP"),
                "Initial deposit",
                Instant.parse("2026-05-15T12:00:00Z")
        );

        assertThrows(UnsupportedOperationException.class, () -> repository.append(entry));
    }
}
