package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.Transaction;
import dev.mjkpotts.picobooks.domain.TransactionType;
import java.time.Instant;
import java.util.UUID;

record TransactionResponse(
        UUID transactionId,
        String accountId,
        TransactionType type,
        MoneyAmountResponse amount,
        MoneyAmountResponse resultingBalance,
        String reference,
        Instant occurredAt
) {

    static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.transactionId(),
                transaction.accountId().value(),
                transaction.type(),
                MoneyAmountResponse.from(transaction.amount()),
                MoneyAmountResponse.from(transaction.resultingBalance()),
                transaction.reference(),
                transaction.occurredAt()
        );
    }
}
