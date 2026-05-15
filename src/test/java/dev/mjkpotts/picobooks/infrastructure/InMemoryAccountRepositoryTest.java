package dev.mjkpotts.picobooks.infrastructure;

import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.mjkpotts.picobooks.domain.Account;
import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.Balance;
import dev.mjkpotts.picobooks.domain.Ledger;
import java.util.List;
import org.junit.jupiter.api.Test;

class InMemoryAccountRepositoryTest {

    private final InMemoryAccountRepository repository = new InMemoryAccountRepository();
    private final AccountId accountId = new AccountId("merchant-123");

    @Test
    void findByIdRemainsSkeleton() {
        assertThrows(UnsupportedOperationException.class, () -> repository.findById(accountId));
    }

    @Test
    void saveRemainsSkeleton() {
        var account = new Account(accountId, new Ledger(List.of(), new Balance(0, "GBP")));

        assertThrows(UnsupportedOperationException.class, () -> repository.save(account));
    }
}
