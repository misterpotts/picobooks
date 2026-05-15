package dev.mjkpotts.tinyledger.api;

import dev.mjkpotts.tinyledger.domain.Money;

record MoneyAmountResponse(
        long amountMinor,
        String currency
) {

    static MoneyAmountResponse from(Money money) {
        return new MoneyAmountResponse(money.amountMinor(), money.currency());
    }
}
