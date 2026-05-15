package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.InvalidLedgerRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
final class ApiExceptionHandler {

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    ApiError notImplemented(UnsupportedOperationException exception) {
        return ApiError.of("not_implemented", exception.getMessage());
    }

    @ExceptionHandler(InvalidLedgerRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError invalidLedgerRequest(InvalidLedgerRequestException exception) {
        return ApiError.of("invalid_ledger_request", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError validationFailure(MethodArgumentNotValidException exception) {
        return ApiError.of("invalid_request", "Request validation failed");
    }
}
