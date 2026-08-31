package com.orthoflow.platform.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Browser origins allowed to call this API.
 *
 * <p>These were six hardcoded strings in {@code SecurityConfig} — two localhost
 * ports and four {@code compaigns.com} hostnames — which meant every deployment
 * of this software shipped with someone else's dev and preprod origins trusted,
 * and adding a customer meant editing Java. They are configuration:
 * {@code orthoflow.cors.allowed-origins} in YAML, or {@code CORS_ALLOWED_ORIGINS}
 * as a comma-separated environment variable.
 *
 * <p>Wildcards are deliberately unsupported. {@code allowCredentials} is on, so
 * a {@code *} origin would be rejected by the browser anyway, and a pattern like
 * {@code https://*.example.com} would trust every subdomain including ones the
 * practice does not control.
 *
 * @param allowedOrigins exact origins, scheme included, no trailing slash
 */
@ConfigurationProperties(prefix = "orthoflow.cors")
public record CorsProperties(List<String> allowedOrigins) {

    public CorsProperties {
        allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
    }
}
