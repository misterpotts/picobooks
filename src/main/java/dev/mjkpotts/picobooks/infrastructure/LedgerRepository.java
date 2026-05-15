package dev.mjkpotts.picobooks.infrastructure;

import dev.mjkpotts.picobooks.domain.AccountLedger;
import dev.mjkpotts.picobooks.domain.AccountId;
import java.util.Optional;
import java.util.function.Function;

public interface LedgerRepository {

    AccountLedger create(AccountLedger ledger);

    Optional<AccountLedger> findById(AccountId accountId);

    <T> T update(AccountId accountId, Function<AccountLedger, T> operation);
}
