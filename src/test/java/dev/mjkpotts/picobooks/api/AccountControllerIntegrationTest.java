package dev.mjkpotts.picobooks.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    private static final String UUID_V7_PATTERN = "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDepositBalanceHistoryFlow() throws Exception {
        var accountId = createAccount("GBP");

        recordTransaction(accountId, "DEPOSIT", 10000, "gbp", "Initial deposit", 10000);
        recordTransaction(accountId, "WITHDRAWAL", 2500, "GBP", "Supplier payment", 7500);
        recordTransaction(accountId, "DEPOSIT", 1250, "GBP", "Card settlement", 8750);

        mockMvc.perform(get("/accounts/{accountId}/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId", equalTo(accountId)))
                .andExpect(jsonPath("$.balance.value", equalTo(8750)))
                .andExpect(jsonPath("$.balance.currency", equalTo("GBP")));

        mockMvc.perform(get("/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].accountId", equalTo(accountId)))
                .andExpect(jsonPath("$[0].type", equalTo("DEPOSIT")))
                .andExpect(jsonPath("$[0].amount.value", equalTo(10000)))
                .andExpect(jsonPath("$[0].resultingBalance.value", equalTo(10000)))
                .andExpect(jsonPath("$[0].reference", equalTo("Initial deposit")))
                .andExpect(jsonPath("$[1].accountId", equalTo(accountId)))
                .andExpect(jsonPath("$[1].type", equalTo("WITHDRAWAL")))
                .andExpect(jsonPath("$[1].amount.value", equalTo(2500)))
                .andExpect(jsonPath("$[1].resultingBalance.value", equalTo(7500)))
                .andExpect(jsonPath("$[1].reference", equalTo("Supplier payment")))
                .andExpect(jsonPath("$[2].accountId", equalTo(accountId)))
                .andExpect(jsonPath("$[2].type", equalTo("DEPOSIT")))
                .andExpect(jsonPath("$[2].amount.value", equalTo(1250)))
                .andExpect(jsonPath("$[2].resultingBalance.value", equalTo(8750)))
                .andExpect(jsonPath("$[2].reference", equalTo("Card settlement")));
    }

    @Test
    void newlyCreatedAccountHasZeroBalanceInCreatedCurrency() throws Exception {
        var accountId = createAccount("EUR");

        mockMvc.perform(get("/accounts/{accountId}/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance.value", equalTo(0)))
                .andExpect(jsonPath("$.balance.currency", equalTo("EUR")));
    }

    @Test
    void unknownAccountReturnsConflict() throws Exception {
        mockMvc.perform(post("/accounts/{accountId}/transactions", uuidV7(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "DEPOSIT",
                                  "amount": {
                                    "value": 100,
                                    "currency": "GBP"
                                  }
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", equalTo("account_not_found")));
    }

    private String createAccount(String currency) throws Exception {
        var result = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currency": "%s"
                                }
                                """.formatted(currency)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId", matchesPattern(UUID_V7_PATTERN)))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), Map.class)
                .get("accountId")
                .toString();
    }

    private void recordTransaction(
            String accountId,
            String type,
            long value,
            String currency,
            String reference,
            long resultingBalance
    ) throws Exception {
        mockMvc.perform(post("/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "%s",
                                  "amount": {
                                    "value": %d,
                                    "currency": "%s"
                                  },
                                  "reference": "%s"
                                }
                                """.formatted(type, value, currency, reference)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId", matchesPattern(UUID_V7_PATTERN)))
                .andExpect(jsonPath("$.accountId", equalTo(accountId)))
                .andExpect(jsonPath("$.type", equalTo(type)))
                .andExpect(jsonPath("$.amount.value", equalTo((int) value)))
                .andExpect(jsonPath("$.amount.currency", equalTo("GBP")))
                .andExpect(jsonPath("$.resultingBalance.value", equalTo((int) resultingBalance)))
                .andExpect(jsonPath("$.resultingBalance.currency", equalTo("GBP")))
                .andExpect(jsonPath("$.reference", equalTo(reference)));
    }

    private static String uuidV7(long seed) {
        var timestamp = 1_765_000_000_000L + seed;
        var mostSignificantBits = (timestamp << 16) | 0x7000L | (seed & 0xfffL);
        var leastSignificantBits = 0x8000000000000000L | (seed & 0x3fffffffffffffffL);
        return new java.util.UUID(mostSignificantBits, leastSignificantBits).toString();
    }
}
