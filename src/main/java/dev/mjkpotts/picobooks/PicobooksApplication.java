package dev.mjkpotts.picobooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the Picobooks Spring Boot ledger application.
 */
@SpringBootApplication
public class PicobooksApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicobooksApplication.class, args);
    }
}
