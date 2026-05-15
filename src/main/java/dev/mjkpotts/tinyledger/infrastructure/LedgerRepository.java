package dev.mjkpotts.tinyledger.infrastructure;

import dev.mjkpotts.tinyledger.domain.AccountId;
import dev.mjkpotts.tinyledger.domain.LedgerEntry;
import java.util.List;

public interface LedgerRepository {

    List<LedgerEntry> findByAccountId(AccountId accountId);

    LedgerEntry append(LedgerEntry entry);
}
