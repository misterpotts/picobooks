package dev.mjkpotts.picobooks.infrastructure;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.LedgerEntry;
import java.util.List;

public interface LedgerRepository {

    List<LedgerEntry> findByAccountId(AccountId accountId);

    LedgerEntry append(LedgerEntry entry);
}
