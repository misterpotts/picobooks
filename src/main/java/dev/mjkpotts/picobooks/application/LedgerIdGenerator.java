package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.TransactionId;

/**
 * Generates producer-owned ledger identifiers.
 */
interface LedgerIdGenerator {

    AccountId nextAccountId();

    TransactionId nextTransactionId();
}
