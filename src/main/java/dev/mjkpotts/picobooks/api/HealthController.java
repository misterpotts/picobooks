package dev.mjkpotts.picobooks.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP health endpoint for local readiness checks.
 */
@RestController
final class HealthController {

    @GetMapping("/health")
    Map<String, String> health() {
        return Map.of(
                "status", "UP"
        );
    }
}
