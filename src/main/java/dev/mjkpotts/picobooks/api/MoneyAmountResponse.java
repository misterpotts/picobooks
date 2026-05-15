package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.Money;

record MoneyAmountResponse(
        long amountMinor,
        String currency
) {

    static MoneyAmountResponse from(Money money) {
        return new MoneyAmountResponse(money.amountMinor(), money.currency());
    }
}
