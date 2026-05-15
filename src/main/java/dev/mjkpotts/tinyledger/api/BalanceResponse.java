package dev.mjkpotts.tinyledger.api;

record BalanceResponse(
        String accountId,
        MoneyAmountResponse balance
) {
}
