package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.Balance;
import dev.mjkpotts.picobooks.domain.Transaction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
final class DefaultAccountService implements AccountService {

    private static final String MESSAGE = "Account implementation intentionally left as a skeleton for Codex-assisted development.";

    @Override
    public Transaction recordTransaction(AccountId accountId, RecordTransactionInput command) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public Balance currentBalance(AccountId accountId) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public List<Transaction> history(AccountId accountId) {
        throw new UnsupportedOperationException(MESSAGE);
    }
}
