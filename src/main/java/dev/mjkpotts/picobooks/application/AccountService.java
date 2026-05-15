package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.Balance;
import dev.mjkpotts.picobooks.domain.Transaction;
import java.util.List;

public interface AccountService {

    Transaction recordTransaction(AccountId accountId, RecordTransactionInput command);

    Balance currentBalance(AccountId accountId);

    List<Transaction> history(AccountId accountId);
}
