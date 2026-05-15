package dev.mjkpotts.picobooks.infrastructure;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.LedgerEntry;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
final class InMemoryLedgerRepository implements LedgerRepository {

    @Override
    public List<LedgerEntry> findByAccountId(AccountId accountId) {
        throw new UnsupportedOperationException("Repository implementation intentionally left as a skeleton.");
    }

    @Override
    public LedgerEntry append(LedgerEntry entry) {
        throw new UnsupportedOperationException("Repository implementation intentionally left as a skeleton.");
    }
}
