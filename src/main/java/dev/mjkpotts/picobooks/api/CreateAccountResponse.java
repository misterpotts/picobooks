package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.application.CreatedAccount;
import java.time.Instant;

/**
 * Response body returned after an account is created.
 */
record CreateAccountResponse(String accountId, Instant createdAt) {

    static CreateAccountResponse from(CreatedAccount account) {
        return new CreateAccountResponse(account.accountId().asString(), account.createdAt());
    }
}
