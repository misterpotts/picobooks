package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.LedgerEntry;
import dev.mjkpotts.picobooks.domain.Money;
import java.util.List;

public interface LedgerService {

    LedgerEntry recordTransaction(AccountId accountId, RecordTransactionInput command);

    Money currentBalance(AccountId accountId);

    List<LedgerEntry> history(AccountId accountId);
}
