package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.Transaction;
import dev.mjkpotts.picobooks.domain.TransactionType;
import java.time.Instant;

record TransactionResponse(
        String transactionId,
        String accountId,
        TransactionType type,
        MoneyAmountResponse amount,
        MoneyAmountResponse resultingBalance,
        String reference,
        Instant occurredAt
) {

    static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.transactionId().asString(),
                transaction.accountId().asString(),
                transaction.type(),
                MoneyAmountResponse.from(transaction.amount()),
                MoneyAmountResponse.from(transaction.resultingBalance()),
                transaction.reference(),
                transaction.occurredAt()
        );
    }
}
