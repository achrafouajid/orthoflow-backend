package com.orthoflow.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Puts one correlation id on every request — in the MDC (so every log line
 * for this request carries it, via the pattern in application.yml), on the
 * response as X-Correlation-Id (so a client/browser can report it back when
 * something goes wrong), and available to GlobalExceptionHandler so a 500's
 * correlationId is the *request's* id, not a fresh one minted at error time.
 * Reuses an inbound X-Correlation-Id if the caller (e.g. the frontend, or a
 * load balancer) already set one, so a trace stays coherent end-to-end
 * instead of getting a new id at every hop.
 */
@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String correlationId = request.getHeader(HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put(MDC_KEY, correlationId);
        response.setHeader(HEADER, correlationId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
