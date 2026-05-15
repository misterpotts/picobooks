package dev.mjkpotts.tinyledger.application;

import dev.mjkpotts.tinyledger.domain.AccountId;
import dev.mjkpotts.tinyledger.domain.LedgerEntry;
import dev.mjkpotts.tinyledger.domain.Money;
import java.util.List;

public interface LedgerService {

    LedgerEntry recordTransaction(AccountId accountId, RecordTransactionCommand command);

    Money currentBalance(AccountId accountId);

    List<LedgerEntry> history(AccountId accountId);
}
