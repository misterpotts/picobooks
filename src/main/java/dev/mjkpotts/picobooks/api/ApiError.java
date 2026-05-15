package dev.mjkpotts.picobooks.api;

import java.time.Instant;

/**
 * Stable error response returned by the HTTP API.
 */
record ApiError(
        String code,
        String message,
        Instant occurredAt
) {

    static ApiError of(String code, String message) {
        return new ApiError(code, message, Instant.now());
    }
}
