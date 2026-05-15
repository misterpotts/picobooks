package dev.mjkpotts.picobooks.api;

import dev.mjkpotts.picobooks.application.LedgerService;
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
final class LedgerController {

    private final LedgerService ledgerService;

    LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/transactions")
    ResponseEntity<LedgerEntryResponse> recordTransaction(
            @PathVariable String accountId,
            @Valid @RequestBody RecordTransactionRequest request
    ) {
        var entry = ledgerService.recordTransaction(new AccountId(accountId), request.toCommand());
        return ResponseEntity.status(201).body(LedgerEntryResponse.from(entry));
    }

    @GetMapping("/balance")
    BalanceResponse balance(@PathVariable String accountId) {
        var balance = ledgerService.currentBalance(new AccountId(accountId));
        return new BalanceResponse(accountId, MoneyAmountResponse.from(balance));
    }

    @GetMapping("/transactions")
    List<LedgerEntryResponse> history(@PathVariable String accountId) {
        return ledgerService.history(new AccountId(accountId))
                .stream()
                .map(LedgerEntryResponse::from)
                .toList();
    }
}
