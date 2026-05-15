package dev.mjkpotts.tinyledger.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.mjkpotts.tinyledger.domain.AccountId;
import dev.mjkpotts.tinyledger.domain.Money;
import dev.mjkpotts.tinyledger.domain.TransactionType;
import org.junit.jupiter.api.Test;

class DefaultLedgerServiceTest {

    private final DefaultLedgerService service = new DefaultLedgerService();
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
