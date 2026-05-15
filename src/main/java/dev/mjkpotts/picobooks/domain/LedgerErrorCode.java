package dev.mjkpotts.picobooks.domain;

public enum LedgerErrorCode {
    ACCOUNT_NOT_FOUND("account_not_found"),
    CURRENCY_MISMATCH("currency_mismatch"),
    INSUFFICIENT_FUNDS("insufficient_funds"),
    INVALID_REQUEST("invalid_request"),
    INVALID_ACCOUNT_ID("invalid_account_id"),
    INVALID_AMOUNT("invalid_amount"),
    INVALID_CURRENCY("invalid_currency"),
    INVALID_TRANSACTION_ID("invalid_transaction_id"),
    INVALID_TRANSACTION_TYPE("invalid_transaction_type");

    private final String wireCode;

    LedgerErrorCode(String wireCode) {
        this.wireCode = wireCode;
    }

    public String wireCode() {
        return wireCode;
    }

    public boolean conflict() {
        return switch (this) {
            case ACCOUNT_NOT_FOUND, CURRENCY_MISMATCH, INSUFFICIENT_FUNDS -> true;
            case INVALID_REQUEST, INVALID_ACCOUNT_ID, INVALID_AMOUNT, INVALID_CURRENCY, INVALID_TRANSACTION_ID,
                 INVALID_TRANSACTION_TYPE -> false;
        };
    }
}
