package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.TransactionId;

interface LedgerIdGenerator {

    AccountId nextAccountId();

    TransactionId nextTransactionId();
}
