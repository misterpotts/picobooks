package dev.mjkpotts.picobooks.api;

record BalanceResponse(
        String accountId,
        MoneyAmountResponse balance
) {
}
