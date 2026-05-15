package dev.mjkpotts.picobooks.api;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.mjkpotts.picobooks.application.CreatedAccount;
import dev.mjkpotts.picobooks.application.LedgerService;
import dev.mjkpotts.picobooks.application.RecordTransactionInput;
import dev.mjkpotts.picobooks.domain.AccountId;
import dev.mjkpotts.picobooks.domain.Balance;
import dev.mjkpotts.picobooks.domain.Transaction;
import dev.mjkpotts.picobooks.domain.TransactionId;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AccountControllerStandaloneTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController(new TestLedgerService()))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void createAccountWithoutCurrencyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("invalid_currency")));
    }

    @Test
    void zeroTransactionAmountReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/accounts/{accountId}/transactions", uuidV7(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson("DEPOSIT", 0, "GBP")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("invalid_amount")));
    }

    @Test
    void negativeTransactionAmountReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/accounts/{accountId}/transactions", uuidV7(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson("DEPOSIT", -1, "GBP")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("invalid_amount")));
    }

    @Test
    void invalidTransactionTypeReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/accounts/{accountId}/transactions", uuidV7(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson("REFUND", 100, "GBP")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("invalid_transaction_type")));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("invalid_request")));
    }

    @Test
    void jsonNullRequestBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("invalid_request")));
    }

    private static String transactionJson(String type, long value, String currency) {
        return """
                {
                  "type": "%s",
                  "amount": {
                    "value": %d,
                    "currency": "%s"
                  }
                }
                """.formatted(type, value, currency);
    }

    private static String uuidV7(long seed) {
        var timestamp = 1_765_000_000_000L + seed;
        var mostSignificantBits = (timestamp << 16) | 0x7000L | (seed & 0xfffL);
        var leastSignificantBits = 0x8000000000000000L | (seed & 0x3fffffffffffffffL);
        return new UUID(mostSignificantBits, leastSignificantBits).toString();
    }

    private static final class TestLedgerService implements LedgerService {

        @Override
        public CreatedAccount createAccount(String currency) {
            return new CreatedAccount(new AccountId(uuidV7(1)), Instant.parse("2026-05-15T12:00:00Z"));
        }

        @Override
        public Transaction recordTransaction(AccountId accountId, RecordTransactionInput command) {
            return new Transaction(
                    new TransactionId(UUID.fromString(uuidV7(2))),
                    accountId,
                    command.type(),
                    command.amount(),
                    new Balance(command.amount().value(), command.amount().currency()),
                    command.reference(),
                    Instant.parse("2026-05-15T12:00:00Z")
            );
        }

        @Override
        public Balance currentBalance(AccountId accountId) {
            return new Balance(0, "GBP");
        }

        @Override
        public List<Transaction> history(AccountId accountId) {
            return List.of();
        }
    }
}
