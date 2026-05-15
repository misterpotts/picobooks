package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.Balance;
import dev.mjkpotts.picobooks.domain.Transaction;
import java.util.List;

/**
 * Application boundary for account creation, ledger writes, and ledger reads.
 */
public interface LedgerService {

    CreatedAccount createAccount(String currency);

    Transaction recordTransaction(AccountId accountId, RecordTransactionInput command);

    Balance currentBalance(AccountId accountId);

    List<Transaction> history(AccountId accountId);
}
