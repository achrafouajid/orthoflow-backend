package com.orthoflow.clinical.application.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record DentalChartResponse(
        UUID id,
        UUID patientId,
        String chartType,
        List<ToothStateResponse> teeth,
        OffsetDateTime updatedAt) {
}
