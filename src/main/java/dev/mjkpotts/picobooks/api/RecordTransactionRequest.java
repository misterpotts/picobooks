package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.application.RecordTransactionInput;
import dev.mjkpotts.picobooks.domain.LedgerErrorCode;
import dev.mjkpotts.picobooks.domain.LedgerException;
import dev.mjkpotts.picobooks.domain.Money;
import dev.mjkpotts.picobooks.domain.TransactionType;
import java.util.Locale;

record RecordTransactionRequest(
        String type,
        MoneyAmountRequest amount,
        String reference
) {

    RecordTransactionInput toCommand() {
        if (type == null || type.isBlank()) {
            throw new LedgerException(LedgerErrorCode.INVALID_TRANSACTION_TYPE, "Transaction type is required");
        }
        if (amount == null) {
            throw new LedgerException(LedgerErrorCode.INVALID_AMOUNT, "Transaction amount is required");
        }
        return new RecordTransactionInput(parseType(), new Money(amount.value(), amount.currency()), reference);
    }

    private TransactionType parseType() {
        try {
            return TransactionType.valueOf(type.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new LedgerException(LedgerErrorCode.INVALID_TRANSACTION_TYPE, "Transaction type must be DEPOSIT or WITHDRAWAL");
        }
    }
}
