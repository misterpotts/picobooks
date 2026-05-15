package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.application.RecordTransactionInput;
import dev.mjkpotts.picobooks.domain.InvalidAmountException;
import dev.mjkpotts.picobooks.domain.InvalidTransactionTypeException;
import dev.mjkpotts.picobooks.domain.TransactionType;
import java.util.Locale;

/**
 * Request body for recording a deposit or withdrawal against one account ledger.
 */
record RecordTransactionRequest(
        String type,
        MoneyAmountRequest amount,
        String reference
) {

    RecordTransactionRequest {
        if (type == null || type.isBlank()) {
            throw new InvalidTransactionTypeException("Transaction type is required");
        }
        if (amount == null) {
            throw new InvalidAmountException("Transaction amount is required");
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
            throw new InvalidTransactionTypeException("Transaction type must be DEPOSIT or WITHDRAWAL");
        }
    }
}
