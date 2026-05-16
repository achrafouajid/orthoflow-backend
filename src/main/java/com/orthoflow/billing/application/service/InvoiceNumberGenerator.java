package com.orthoflow.billing.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InvoiceNumberGenerator {

    private final JdbcTemplate jdbcTemplate;

    public String generate(String regionCode) {
        Long sequenceValue = jdbcTemplate.queryForObject("SELECT nextval('invoices_seq')", Long.class);
        int year = LocalDate.now().getYear();
        return String.format("INV-%d-%s-%05d", year, regionCode, sequenceValue);
    }
}
