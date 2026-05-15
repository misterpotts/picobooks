package dev.mjkpotts.picobooks.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.mjkpotts.picobooks.application.AccountService;
import dev.mjkpotts.picobooks.application.RecordTransactionInput;
import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.Balance;
import dev.mjkpotts.picobooks.domain.Money;
import dev.mjkpotts.picobooks.domain.Transaction;
import dev.mjkpotts.picobooks.domain.TransactionType;
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

@WebMvcTest(AccountController.class)
@Import({ApiExceptionHandler.class, AccountControllerIntegrationTest.TestAccountServiceConfiguration.class})
class AccountControllerIntegrationTest {

    private static final UUID TRANSACTION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Instant OCCURRED_AT = Instant.parse("2026-05-15T12:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void recordTransactionReturnsNotImplementedUntilAccountIsBuilt() throws Exception {
        mockMvc.perform(post("/accounts/merchant-123/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "DEPOSIT",
                                  "amount": {
                                    "value": 10000,
                                    "currency": "GBP"
                                  },
                                  "reference": "not-implemented"
                                }
                                """))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.code", equalTo("not_implemented")));
    }

    @Test
    void recordTransactionReturnsCreatedTransaction() throws Exception {
        mockMvc.perform(post("/accounts/merchant-123/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "DEPOSIT",
                                  "amount": {
                                    "value": 10000,
                                    "currency": "gbp"
                                  },
                                  "reference": "Initial deposit"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId", equalTo(TRANSACTION_ID.toString())))
                .andExpect(jsonPath("$.accountId", equalTo("merchant-123")))
                .andExpect(jsonPath("$.type", equalTo("DEPOSIT")))
                .andExpect(jsonPath("$.amount.value", equalTo(10000)))
                .andExpect(jsonPath("$.amount.currency", equalTo("GBP")))
                .andExpect(jsonPath("$.resultingBalance.value", equalTo(10000)))
                .andExpect(jsonPath("$.resultingBalance.currency", equalTo("GBP")))
                .andExpect(jsonPath("$.reference", equalTo("Initial deposit")))
                .andExpect(jsonPath("$.occurredAt", equalTo("2026-05-15T12:00:00Z")));
    }

    @Test
    void balanceReturnsCurrentBalance() throws Exception {
        mockMvc.perform(get("/accounts/merchant-123/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId", equalTo("merchant-123")))
                .andExpect(jsonPath("$.balance.value", equalTo(12500)))
                .andExpect(jsonPath("$.balance.currency", equalTo("GBP")));
    }

    @Test
    void historyReturnsTransactions() throws Exception {
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
                                    "value": 0,
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
                                    "value": 10000,
                                    "currency": "GB"
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("invalid_request")));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestAccountServiceConfiguration {

        @Bean
        AccountService accountService() {
            return new AccountService() {
                @Override
                public Transaction recordTransaction(
                        AccountId accountId,
                        RecordTransactionInput command
                ) {
                    if ("not-implemented".equals(command.reference())) {
                        throw new UnsupportedOperationException("Account implementation intentionally left as a skeleton for Codex-assisted development.");
                    }
                    return transaction(accountId);
                }

                @Override
                public Balance currentBalance(AccountId accountId) {
                    return new Balance(12500, "GBP");
                }

                @Override
                public List<Transaction> history(AccountId accountId) {
                    return List.of(transaction(accountId));
                }
            };
        }

        private static Transaction transaction(AccountId accountId) {
            return new Transaction(
                    TRANSACTION_ID,
                    accountId,
                    TransactionType.DEPOSIT,
                    new Money(10000, "GBP"),
                    new Balance(10000, "GBP"),
                    "Initial deposit",
                    OCCURRED_AT
            );
        }
    }
}
