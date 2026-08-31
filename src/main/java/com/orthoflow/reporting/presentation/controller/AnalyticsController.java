package com.orthoflow.reporting.presentation.controller;

import com.orthoflow.reporting.application.dto.InventoryKPIResponse;
import com.orthoflow.reporting.application.dto.TreatmentProfitabilityResponse;
import com.orthoflow.reporting.application.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stock/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/treatment-profitability")
    public ResponseEntity<List<TreatmentProfitabilityResponse>> getTreatmentProfitability() {
        return ResponseEntity.ok(analyticsService.getTreatmentProfitability());
    }

    @GetMapping("/kpi")
    public ResponseEntity<InventoryKPIResponse> getInventoryKPI() {
        return ResponseEntity.ok(analyticsService.getInventoryKPI());
    }
}
