package dev.mjkpotts.picobooks.infrastructure;

import dev.mjkpotts.picobooks.domain.AccountLedger;
import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.LedgerErrorCode;
import dev.mjkpotts.picobooks.domain.LedgerException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.springframework.stereotype.Repository;

@Repository
final class InMemoryLedgerRepository implements LedgerRepository {

    private final ConcurrentHashMap<AccountId, AccountLedger> ledgers = new ConcurrentHashMap<>();

    @Override
    public AccountLedger create(AccountLedger ledger) {
        var existing = ledgers.putIfAbsent(ledger.accountId(), ledger);
        if (existing != null) {
            throw new LedgerException(LedgerErrorCode.INVALID_ACCOUNT_ID, "Account already exists");
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
                throw new LedgerException(LedgerErrorCode.ACCOUNT_NOT_FOUND, "Account not found: " + accountId.asString());
            }
            result.set(operation.apply(ledger));
            return ledger;
        });
        return result.get();
    }
}
