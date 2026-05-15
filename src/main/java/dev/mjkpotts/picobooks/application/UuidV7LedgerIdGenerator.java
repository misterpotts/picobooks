package dev.mjkpotts.picobooks.application;

import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.TransactionId;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * UUID v7 ledger identifier generator backed by the system clock and secure randomness.
 */
@Component
final class UuidV7LedgerIdGenerator implements LedgerIdGenerator {

    private static final long UUID_V7_VERSION_BITS = 0x7000L;
    private static final long UUID_VARIANT_BITS = 0x8000000000000000L;
    private static final long UUID_VARIANT_RANDOM_MASK = 0x3fffffffffffffffL;
    private static final long UUID_TIMESTAMP_MASK = 0x0000ffffffffffffL;
    private static final long UUID_RANDOM_A_MASK = 0xfffL;

    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    UuidV7LedgerIdGenerator(Clock clock) {
        this.clock = clock;
    }

    @Override
    public AccountId nextAccountId() {
        return new AccountId(nextUuidV7());
    }

    @Override
    public TransactionId nextTransactionId() {
        return new TransactionId(nextUuidV7());
    }

    private UUID nextUuidV7() {
        var timestamp = clock.millis() & UUID_TIMESTAMP_MASK;
        var randomA = random.nextLong() & UUID_RANDOM_A_MASK;
        var mostSignificantBits = (timestamp << 16) | UUID_V7_VERSION_BITS | randomA;
        var leastSignificantBits = UUID_VARIANT_BITS | (random.nextLong() & UUID_VARIANT_RANDOM_MASK);
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
