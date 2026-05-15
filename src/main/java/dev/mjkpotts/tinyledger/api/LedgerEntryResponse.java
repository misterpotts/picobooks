package dev.mjkpotts.tinyledger.api;

import dev.mjkpotts.tinyledger.domain.LedgerEntry;
import dev.mjkpotts.tinyledger.domain.MovementType;
import java.time.Instant;
import java.util.UUID;

record LedgerEntryResponse(
        UUID transactionId,
        String accountId,
        MovementType type,
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
