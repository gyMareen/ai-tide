package com.ai.tide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * AI-Tide Application
 * Main Spring Boot application entry point
 */
@SpringBootApplication
@EnableJpaAuditing
public class AiTideApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiTideApplication.class, args);
    }
}
