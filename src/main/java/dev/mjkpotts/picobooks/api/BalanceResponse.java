package dev.mjkpotts.picobooks.api;

/**
 * Current balance response for one account.
 */
record BalanceResponse(
        String accountId,
        MoneyAmountResponse balance
) {
}
