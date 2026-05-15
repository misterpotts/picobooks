package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.application.AccountService;
import dev.mjkpotts.picobooks.domain.AccountId;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts/{accountId}")
final class AccountController {

    private final AccountService accountService;

    AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/transactions")
    ResponseEntity<TransactionResponse> recordTransaction(
            @PathVariable String accountId,
            @Valid @RequestBody RecordTransactionRequest request
    ) {
        var transaction = accountService.recordTransaction(new AccountId(accountId), request.toCommand());
        return ResponseEntity.status(201).body(TransactionResponse.from(transaction));
    }

    @GetMapping("/balance")
    BalanceResponse balance(@PathVariable String accountId) {
        var balance = accountService.currentBalance(new AccountId(accountId));
        return new BalanceResponse(accountId, MoneyAmountResponse.from(balance));
    }

    @GetMapping("/transactions")
    List<TransactionResponse> history(@PathVariable String accountId) {
        return accountService.history(new AccountId(accountId))
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }
}
