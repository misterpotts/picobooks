package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.application.RecordTransactionInput;
import dev.mjkpotts.picobooks.domain.LedgerErrorCode;
import dev.mjkpotts.picobooks.domain.LedgerException;
import dev.mjkpotts.picobooks.domain.TransactionType;
import java.util.Locale;

record RecordTransactionRequest(
        String type,
        MoneyAmountRequest amount,
        String reference
) {

    RecordTransactionRequest {
        if (type == null || type.isBlank()) {
            throw new LedgerException(LedgerErrorCode.INVALID_TRANSACTION_TYPE, "Transaction type is required");
        }
        if (amount == null) {
            throw new LedgerException(LedgerErrorCode.INVALID_AMOUNT, "Transaction amount is required");
        }
        type = type.trim().toUpperCase(Locale.ROOT);
        parseType(type);
    }

    RecordTransactionInput toCommand() {
        return new RecordTransactionInput(parseType(type), amount.toMoney(), reference);
    }

    private static TransactionType parseType(String type) {
        try {
            return TransactionType.valueOf(type);
        } catch (IllegalArgumentException exception) {
            throw new LedgerException(LedgerErrorCode.INVALID_TRANSACTION_TYPE, "Transaction type must be DEPOSIT or WITHDRAWAL");
        }
    }
}
