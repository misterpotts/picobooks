package dev.mjkpotts.tinyledger.application;

import dev.mjkpotts.tinyledger.domain.AccountId;
import dev.mjkpotts.tinyledger.domain.LedgerEntry;
import dev.mjkpotts.tinyledger.domain.Money;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
final class DefaultLedgerService implements LedgerService {

    private static final String MESSAGE = "Ledger implementation intentionally left as a skeleton for Codex-assisted development.";

    @Override
    public LedgerEntry recordTransaction(AccountId accountId, RecordTransactionInput command) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public Money currentBalance(AccountId accountId) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public List<LedgerEntry> history(AccountId accountId) {
        throw new UnsupportedOperationException(MESSAGE);
    }
}
