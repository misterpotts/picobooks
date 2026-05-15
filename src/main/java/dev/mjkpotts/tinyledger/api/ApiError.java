package dev.mjkpotts.tinyledger.api;

import java.time.Instant;

record ApiError(
        String code,
        String message,
        Instant occurredAt
) {

    static ApiError of(String code, String message) {
        return new ApiError(code, message, Instant.now());
    }
}
