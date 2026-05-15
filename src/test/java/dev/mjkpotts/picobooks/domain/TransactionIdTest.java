package dev.mjkpotts.picobooks.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransactionIdTest {

    @Test
    void keepsProvidedTransactionId() {
        var uuid = uuidV7(1);
        var transactionId = new TransactionId(uuid);

        assertEquals(uuid, transactionId.value());
        assertEquals(uuid.toString(), transactionId.asString());
    }

    @Test
    void rejectsNonUuidV7TransactionId() {
        var exception = assertThrows(LedgerException.class, () -> new TransactionId(UUID.randomUUID()));

        assertEquals(LedgerErrorCode.INVALID_TRANSACTION_ID, exception.code());
    }

    private static UUID uuidV7(long seed) {
        var timestamp = 1_765_000_000_000L + seed;
        var mostSignificantBits = (timestamp << 16) | 0x7000L | (seed & 0xfffL);
        var leastSignificantBits = 0x8000000000000000L | (seed & 0x3fffffffffffffffL);
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
