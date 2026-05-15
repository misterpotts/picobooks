package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountLedger;
import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.Balance;
import dev.mjkpotts.picobooks.domain.LedgerErrorCode;
import dev.mjkpotts.picobooks.domain.LedgerException;
import dev.mjkpotts.picobooks.domain.Transaction;
import dev.mjkpotts.picobooks.infrastructure.LedgerRepository;
import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
final class DefaultLedgerService implements LedgerService {

    private final LedgerRepository repository;
    private final LedgerIdGenerator idGenerator;
    private final Clock clock;

    DefaultLedgerService(
            LedgerRepository repository,
            LedgerIdGenerator idGenerator,
            Clock clock
    ) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.clock = clock;
    }

    @Override
    public CreatedAccount createAccount(String currency) {
        var accountId = idGenerator.nextAccountId();
        var createdAt = clock.instant();
        var ledger = AccountLedger.create(accountId, currency, createdAt);
        repository.create(ledger);
        return new CreatedAccount(accountId, createdAt);
    }

    @Override
    public Transaction recordTransaction(AccountId accountId, RecordTransactionInput command) {
        if (command == null) {
            throw new LedgerException(LedgerErrorCode.INVALID_AMOUNT, "Transaction amount is required");
        }
        return repository.update(accountId, ledger -> ledger.record(
                command.type(),
                command.amount(),
                command.reference(),
                idGenerator.nextTransactionId(),
                clock.instant()
        ));
    }

    @Override
    public Balance currentBalance(AccountId accountId) {
        return repository.findById(accountId)
                .map(AccountLedger::currentBalance)
                .orElseThrow(() -> notFound(accountId));
    }

    @Override
    public List<Transaction> history(AccountId accountId) {
        return repository.findById(accountId)
                .map(AccountLedger::entries)
                .orElseThrow(() -> notFound(accountId));
    }

    private LedgerException notFound(AccountId accountId) {
        return new LedgerException(LedgerErrorCode.ACCOUNT_NOT_FOUND, "Account not found: " + accountId.asString());
    }
}
