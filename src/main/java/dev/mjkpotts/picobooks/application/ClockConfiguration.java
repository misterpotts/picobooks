package dev.mjkpotts.picobooks.application;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides application time sources for ledger services.
 */
@Configuration(proxyBeanMethods = false)
class ClockConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
