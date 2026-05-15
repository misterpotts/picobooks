package dev.mjkpotts.picobooks.domain;

/**
 * Raised when a ledger operation targets an account that has not been created.
 */
public final class AccountNotFoundException extends LedgerException {

    private static final String ERROR_CODE = "account_not_found";

    public AccountNotFoundException(AccountId accountId) {
        super(ERROR_CODE, "Account not found: " + accountId.asString());
    }
}
