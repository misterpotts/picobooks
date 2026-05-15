package dev.mjkpotts.picobooks.infrastructure;

import dev.mjkpotts.picobooks.domain.AccountLedger;
import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.AccountNotFoundException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.springframework.stereotype.Repository;

/**
 * In-memory account-partitioned ledger repository for the assessment runtime.
 */
@Repository
final class InMemoryLedgerRepository implements LedgerRepository {

    private final ConcurrentHashMap<AccountId, AccountLedger> ledgers = new ConcurrentHashMap<>();

    @Override
    public AccountLedger create(AccountLedger ledger) {
        var existing = ledgers.putIfAbsent(ledger.accountId(), ledger);
        if (existing != null) {
            throw new IllegalStateException("Account already exists: " + ledger.accountId().asString());
        }
        return ledger;
    }

    @Override
    public Optional<AccountLedger> findById(AccountId accountId) {
        return Optional.ofNullable(ledgers.get(accountId));
    }

    @Override
    public <T> T update(AccountId accountId, Function<AccountLedger, T> operation) {
        var result = new AtomicReference<T>();
        ledgers.compute(accountId, (ignored, ledger) -> {
            if (ledger == null) {
                throw new AccountNotFoundException(accountId);
            }
            result.set(operation.apply(ledger));
            return ledger;
        });
        return result.get();
    }
}
