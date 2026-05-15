package dev.mjkpotts.picobooks.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.Money;
import dev.mjkpotts.picobooks.domain.TransactionType;
import org.junit.jupiter.api.Test;

class DefaultAccountServiceTest {

    private final DefaultAccountService service = new DefaultAccountService();
    private final AccountId accountId = new AccountId("merchant-123");

    @Test
    void recordTransactionRemainsSkeleton() {
        var command = new RecordTransactionInput(TransactionType.DEPOSIT, new Money(100, "GBP"), "Initial deposit");

        assertThrows(UnsupportedOperationException.class, () -> service.recordTransaction(accountId, command));
    }

    @Test
    void currentBalanceRemainsSkeleton() {
        assertThrows(UnsupportedOperationException.class, () -> service.currentBalance(accountId));
    }

    @Test
    void historyRemainsSkeleton() {
        assertThrows(UnsupportedOperationException.class, () -> service.history(accountId));
    }
}
