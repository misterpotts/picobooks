package dev.mjkpotts.tinyledger.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.mjkpotts.tinyledger.application.LedgerService;
import dev.mjkpotts.tinyledger.application.RecordTransactionInput;
import dev.mjkpotts.tinyledger.domain.AccountId;
import dev.mjkpotts.tinyledger.domain.LedgerEntry;
import dev.mjkpotts.tinyledger.domain.Money;
import dev.mjkpotts.tinyledger.domain.TransactionType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LedgerController.class)
@Import({ApiExceptionHandler.class, LedgerControllerIntegrationTest.TestLedgerServiceConfiguration.class})
class LedgerControllerIntegrationTest {

    private static final UUID TRANSACTION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Instant OCCURRED_AT = Instant.parse("2026-05-15T12:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void recordTransactionReturnsNotImplementedUntilLedgerIsBuilt() throws Exception {
        mockMvc.perform(post("/accounts/merchant-123/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "DEPOSIT",
                                  "amount": {
                                    "amountMinor": 10000,
                                    "currency": "GBP"
                                  },
                                  "reference": "not-implemented"
                                }
                                """))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.code", equalTo("not_implemented")));
    }

    @Test
    void recordTransactionReturnsCreatedEntry() throws Exception {
        mockMvc.perform(post("/accounts/merchant-123/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "DEPOSIT",
                                  "amount": {
                                    "amountMinor": 10000,
                                    "currency": "gbp"
                                  },
                                  "reference": "Initial deposit"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId", equalTo(TRANSACTION_ID.toString())))
                .andExpect(jsonPath("$.accountId", equalTo("merchant-123")))
                .andExpect(jsonPath("$.type", equalTo("DEPOSIT")))
                .andExpect(jsonPath("$.amount.amountMinor", equalTo(10000)))
                .andExpect(jsonPath("$.amount.currency", equalTo("GBP")))
                .andExpect(jsonPath("$.resultingBalance.amountMinor", equalTo(10000)))
                .andExpect(jsonPath("$.resultingBalance.currency", equalTo("GBP")))
                .andExpect(jsonPath("$.reference", equalTo("Initial deposit")))
                .andExpect(jsonPath("$.occurredAt", equalTo("2026-05-15T12:00:00Z")));
    }

    @Test
    void balanceReturnsCurrentBalance() throws Exception {
        mockMvc.perform(get("/accounts/merchant-123/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId", equalTo("merchant-123")))
                .andExpect(jsonPath("$.balance.amountMinor", equalTo(12500)))
                .andExpect(jsonPath("$.balance.currency", equalTo("GBP")));
    }

    @Test
    void historyReturnsLedgerEntries() throws Exception {
        mockMvc.perform(get("/accounts/merchant-123/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].transactionId", equalTo(TRANSACTION_ID.toString())))
                .andExpect(jsonPath("$[0].accountId", equalTo("merchant-123")))
                .andExpect(jsonPath("$[0].type", equalTo("DEPOSIT")));
    }

    @Test
    void recordTransactionRejectsNonPositiveMoney() throws Exception {
        mockMvc.perform(post("/accounts/merchant-123/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "DEPOSIT",
                                  "amount": {
                                    "amountMinor": 0,
                                    "currency": "GBP"
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("invalid_request")));
    }

    @Test
    void recordTransactionRejectsInvalidCurrency() throws Exception {
        mockMvc.perform(post("/accounts/merchant-123/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "DEPOSIT",
                                  "amount": {
                                    "amountMinor": 10000,
                                    "currency": "GB"
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("invalid_ledger_request")));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestLedgerServiceConfiguration {

        @Bean
        LedgerService ledgerService() {
            return new LedgerService() {
                @Override
                public LedgerEntry recordTransaction(
                        AccountId accountId,
                        RecordTransactionInput command
                ) {
                    if ("not-implemented".equals(command.reference())) {
                        throw new UnsupportedOperationException("Ledger implementation intentionally left as a skeleton for Codex-assisted development.");
                    }
                    return entry(accountId);
                }

                @Override
                public Money currentBalance(AccountId accountId) {
                    return new Money(12500, "GBP");
                }

                @Override
                public List<LedgerEntry> history(AccountId accountId) {
                    return List.of(entry(accountId));
                }
            };
        }

        private static LedgerEntry entry(AccountId accountId) {
            return new LedgerEntry(
                    TRANSACTION_ID,
                    accountId,
                    TransactionType.DEPOSIT,
                    new Money(10000, "GBP"),
                    new Money(10000, "GBP"),
                    "Initial deposit",
                    OCCURRED_AT
            );
        }
    }
}
