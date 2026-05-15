package dev.mjkpotts.tinyledger.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
final class HealthController {

    @GetMapping("/health")
    Map<String, String> health() {
        return Map.of(
                "status", "UP"
        );
    }
}
