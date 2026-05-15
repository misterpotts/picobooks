package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.AccountNotFoundException;
import dev.mjkpotts.picobooks.domain.CurrencyMismatchException;
import dev.mjkpotts.picobooks.domain.InsufficientFundsException;
import dev.mjkpotts.picobooks.domain.InvalidAccountIdException;
import dev.mjkpotts.picobooks.domain.InvalidAmountException;
import dev.mjkpotts.picobooks.domain.InvalidCurrencyException;
import dev.mjkpotts.picobooks.domain.InvalidRequestException;
import dev.mjkpotts.picobooks.domain.InvalidTransactionIdException;
import dev.mjkpotts.picobooks.domain.InvalidTransactionTypeException;
import dev.mjkpotts.picobooks.domain.LedgerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps domain and request parsing failures to stable HTTP error responses.
 */
@RestControllerAdvice
final class ApiExceptionHandler {

    @ExceptionHandler(LedgerException.class)
    ResponseEntity<ApiError> ledgerFailure(LedgerException exception) {
        return ResponseEntity.status(statusFor(exception))
                .body(ApiError.of(exception.wireCode(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError validationFailure(MethodArgumentNotValidException exception) {
        return ApiError.of(new InvalidRequestException("Request validation failed").wireCode(), "Request validation failed");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> unreadableMessage(HttpMessageNotReadableException exception) {
        var ledgerException = nestedLedgerException(exception);
        if (ledgerException != null) {
            return ledgerFailure(ledgerException);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(new InvalidRequestException("Request body is invalid").wireCode(), "Request body is invalid"));
    }

    private static HttpStatus statusFor(LedgerException exception) {
        if (exception instanceof AccountNotFoundException
                || exception instanceof CurrencyMismatchException
                || exception instanceof InsufficientFundsException) {
            return HttpStatus.CONFLICT;
        }
        if (exception instanceof InvalidAccountIdException
                || exception instanceof InvalidAmountException
                || exception instanceof InvalidCurrencyException
                || exception instanceof InvalidRequestException
                || exception instanceof InvalidTransactionIdException
                || exception instanceof InvalidTransactionTypeException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private static LedgerException nestedLedgerException(Throwable exception) {
        var cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof LedgerException ledgerException) {
                return ledgerException;
            }
            cause = cause.getCause();
        }
        return null;
    }
}
