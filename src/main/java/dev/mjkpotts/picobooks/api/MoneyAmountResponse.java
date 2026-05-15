package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.Balance;
import dev.mjkpotts.picobooks.domain.Money;

record MoneyAmountResponse(
        long value,
        String currency
) {

    static MoneyAmountResponse from(Money money) {
        return new MoneyAmountResponse(money.value(), money.currency());
    }

    static MoneyAmountResponse from(Balance balance) {
        return new MoneyAmountResponse(balance.value(), balance.currency());
    }
}
