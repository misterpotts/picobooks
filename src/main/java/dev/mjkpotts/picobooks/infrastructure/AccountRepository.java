package dev.mjkpotts.picobooks.infrastructure;

import dev.mjkpotts.picobooks.domain.Account;
import dev.mjkpotts.picobooks.domain.AccountId;
import java.util.Optional;

public interface AccountRepository {

    Optional<Account> findById(AccountId accountId);

    Account save(Account account);
}
