package com.orthoflow.clinical.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.clinical.application.dto.DentalChartResponse;
import com.orthoflow.clinical.application.dto.UpdateToothStateRequest;
import com.orthoflow.clinical.application.service.DentalChartService;
import com.orthoflow.clinical.domain.model.ToothStateEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients/{patientId}/dental-chart")
@RequiredArgsConstructor
public class DentalChartController {

    private final DentalChartService dentalChartService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public DentalChartResponse getChart(
            @PathVariable UUID patientId,
            @RequestParam(required = false) String chartType) {
        return dentalChartService.getOrCreateChart(patientId, chartType);
    }

    @PutMapping("/teeth/{fdi}")
    public DentalChartResponse updateTooth(
            @PathVariable UUID patientId,
            @PathVariable String fdi,
            @Valid @RequestBody UpdateToothStateRequest request) {
        return dentalChartService.updateTooth(patientId, fdi, request, currentUserProvider.requireUserId());
    }

    @GetMapping("/events")
    public List<ToothStateEvent> getEvents(@PathVariable UUID patientId) {
        return dentalChartService.getEvents(patientId);
    }
}
