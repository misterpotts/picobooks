package dev.mjkpotts.tinyledger.api;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.mjkpotts.tinyledger.application.LedgerService;
import dev.mjkpotts.tinyledger.application.RecordTransactionInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LedgerController.class)
@Import({ApiExceptionHandler.class, LedgerControllerSkeletonTest.TestLedgerServiceConfiguration.class})
class LedgerControllerSkeletonTest {

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
                                  "reference": "Initial deposit"
                                }
                                """))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.code", equalTo("not_implemented")));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestLedgerServiceConfiguration {

        @Bean
        LedgerService ledgerService() {
            return new LedgerService() {
                @Override
                public dev.mjkpotts.tinyledger.domain.LedgerEntry recordTransaction(
                        dev.mjkpotts.tinyledger.domain.AccountId accountId,
                        RecordTransactionInput command
                ) {
                    throw new UnsupportedOperationException("Ledger implementation intentionally left as a skeleton for Codex-assisted development.");
                }

                @Override
                public dev.mjkpotts.tinyledger.domain.Money currentBalance(
                        dev.mjkpotts.tinyledger.domain.AccountId accountId
                ) {
                    throw new UnsupportedOperationException("Ledger implementation intentionally left as a skeleton for Codex-assisted development.");
                }

                @Override
                public java.util.List<dev.mjkpotts.tinyledger.domain.LedgerEntry> history(
                        dev.mjkpotts.tinyledger.domain.AccountId accountId
                ) {
                    throw new UnsupportedOperationException("Ledger implementation intentionally left as a skeleton for Codex-assisted development.");
                }
            };
        }
    }
}
