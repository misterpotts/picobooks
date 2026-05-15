package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.domain.LedgerErrorCode;
import dev.mjkpotts.picobooks.domain.LedgerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
final class ApiExceptionHandler {

    @ExceptionHandler(LedgerException.class)
    ResponseEntity<ApiError> ledgerFailure(LedgerException exception) {
        var status = exception.code().conflict() ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiError.of(exception.code().wireCode(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError validationFailure(MethodArgumentNotValidException exception) {
        return ApiError.of(LedgerErrorCode.INVALID_REQUEST.wireCode(), "Request validation failed");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> unreadableMessage(HttpMessageNotReadableException exception) {
        var ledgerException = nestedLedgerException(exception);
        if (ledgerException != null) {
            return ledgerFailure(ledgerException);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(LedgerErrorCode.INVALID_REQUEST.wireCode(), "Request body is invalid"));
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
