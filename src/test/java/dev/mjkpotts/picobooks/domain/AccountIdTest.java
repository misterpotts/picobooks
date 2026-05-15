package dev.mjkpotts.picobooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountIdTest {

    @Test
    void keepsProvidedAccountId() {
        var uuid = uuidV7(1);
        var accountId = new AccountId(uuid.toString());

        assertEquals(uuid, accountId.value());
        assertEquals(uuid.toString(), accountId.asString());
    }

    @Test
    void rejectsNonUuidV7AccountId() {
        var exception = assertThrows(LedgerException.class, () -> new AccountId("merchant-123"));

        assertEquals(LedgerErrorCode.INVALID_ACCOUNT_ID, exception.code());
    }

    private static UUID uuidV7(long seed) {
        var timestamp = 1_765_000_000_000L + seed;
        var mostSignificantBits = (timestamp << 16) | 0x7000L | (seed & 0xfffL);
        var leastSignificantBits = 0x8000000000000000L | (seed & 0x3fffffffffffffffL);
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
