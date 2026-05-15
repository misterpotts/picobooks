package dev.mjkpotts.picobooks.domain;

/**
 * Raised when a request body is absent or malformed beyond a field-specific validation error.
 */
public final class InvalidRequestException extends LedgerException {

    private static final String ERROR_CODE = "invalid_request";

    public InvalidRequestException(String message) {
        super(ERROR_CODE, message);
    }
}
