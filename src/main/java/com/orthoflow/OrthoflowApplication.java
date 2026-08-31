package com.orthoflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application entry point.
 *
 * <p>This was {@code com.orthoflow.billing.BillingApplication} — the whole
 * application booting from inside one feature module, which is also why
 * {@code scanBasePackages}, {@code @EnableJpaRepositories} and
 * {@code @EntityScan} all had to be widened back out to {@code com.orthoflow}
 * by hand. From the root package those three annotations are redundant with
 * Spring Boot's defaults, but they are kept explicit so that moving this class
 * again cannot silently drop half the application's beans.
 */
@SpringBootApplication(scanBasePackages = "com.orthoflow")
@EnableJpaRepositories(basePackages = "com.orthoflow")
@EntityScan(basePackages = "com.orthoflow")
public class OrthoflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrthoflowApplication.class, args);
    }
}
