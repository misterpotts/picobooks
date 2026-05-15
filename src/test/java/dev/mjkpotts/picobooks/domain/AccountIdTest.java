package dev.mjkpotts.picobooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AccountIdTest {

    @Test
    void keepsProvidedAccountId() {
        var accountId = new AccountId("merchant-123");

        assertEquals("merchant-123", accountId.value());
    }

    @Test
    void rejectsBlankAccountId() {
        var exception = assertThrows(InvalidDomainRequestException.class, () -> new AccountId(" "));

        assertEquals("accountId must not be blank", exception.getMessage());
    }
}
