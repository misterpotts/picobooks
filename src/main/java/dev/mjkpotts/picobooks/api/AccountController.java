package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.application.LedgerService;
import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.LedgerErrorCode;
import dev.mjkpotts.picobooks.domain.LedgerException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
final class AccountController {

    private final LedgerService ledgerService;

    AccountController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping
    ResponseEntity<CreateAccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        var accountRequest = requireRequest(request);
        var account = ledgerService.createAccount(accountRequest.currency());
        return ResponseEntity.status(201).body(CreateAccountResponse.from(account));
    }

    @PostMapping("/{accountId}/transactions")
    ResponseEntity<TransactionResponse> recordTransaction(
            @PathVariable String accountId,
            @RequestBody RecordTransactionRequest request
    ) {
        var transactionRequest = requireRequest(request);
        var transaction = ledgerService.recordTransaction(new AccountId(accountId), transactionRequest.toCommand());
        return ResponseEntity.status(201).body(TransactionResponse.from(transaction));
    }

    @GetMapping("/{accountId}/balance")
    BalanceResponse balance(@PathVariable String accountId) {
        var balance = ledgerService.currentBalance(new AccountId(accountId));
        return new BalanceResponse(accountId, MoneyAmountResponse.from(balance));
    }

    @GetMapping("/{accountId}/transactions")
    List<TransactionResponse> history(@PathVariable String accountId) {
        return ledgerService.history(new AccountId(accountId))
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    private static CreateAccountRequest requireRequest(CreateAccountRequest request) {
        if (request == null) {
            throw new LedgerException(LedgerErrorCode.INVALID_REQUEST, "Request body is required");
        }
        return request;
    }

    private static RecordTransactionRequest requireRequest(RecordTransactionRequest request) {
        if (request == null) {
            throw new LedgerException(LedgerErrorCode.INVALID_REQUEST, "Request body is required");
        }
        return request;
    }
}
