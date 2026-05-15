package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.LedgerEntry;
import dev.mjkpotts.picobooks.domain.TransactionType;
import java.time.Instant;
import java.util.UUID;

record LedgerEntryResponse(
        UUID transactionId,
        String accountId,
        TransactionType type,
        MoneyAmountResponse amount,
        MoneyAmountResponse resultingBalance,
        String reference,
        Instant occurredAt
) {

    static LedgerEntryResponse from(LedgerEntry entry) {
        return new LedgerEntryResponse(
                entry.transactionId(),
                entry.accountId().value(),
                entry.type(),
                MoneyAmountResponse.from(entry.amount()),
                MoneyAmountResponse.from(entry.resultingBalance()),
                entry.reference(),
                entry.occurredAt()
        );
    }
}
