package dev.mjkpotts.picobooks.domain;

public final class InvalidDomainRequestException extends RuntimeException {

    public InvalidDomainRequestException(String message) {
        super(message);
    }
}
