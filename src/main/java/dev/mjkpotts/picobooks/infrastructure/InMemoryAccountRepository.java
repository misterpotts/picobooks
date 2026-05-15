package dev.mjkpotts.picobooks.infrastructure;

import dev.mjkpotts.picobooks.domain.Account;
import dev.mjkpotts.picobooks.domain.AccountId;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
final class InMemoryAccountRepository implements AccountRepository {

    @Override
    public Optional<Account> findById(AccountId accountId) {
        throw new UnsupportedOperationException("Repository implementation intentionally left as a skeleton.");
    }

    @Override
    public Account save(Account account) {
        throw new UnsupportedOperationException("Repository implementation intentionally left as a skeleton.");
    }
}
