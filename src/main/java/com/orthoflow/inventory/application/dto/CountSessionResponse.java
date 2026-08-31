package com.orthoflow.inventory.application.dto;

import com.orthoflow.inventory.domain.model.CountSession;
import com.orthoflow.inventory.domain.model.CountSessionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record CountSessionResponse(
        UUID id,
        String sessionNumber,
        CountSessionStatus status,
        OffsetDateTime snapshotDate,
        LocalDate countDate,
        OffsetDateTime validatedDate,
        List<CountSessionLineResponse> lines,
        BigDecimal totalQuantityVariance,
        BigDecimal totalCostVariance,
        String notes,
        UUID createdBy,
        UUID validatedBy,
        OffsetDateTime createdAt
) {
    public static CountSessionResponse from(CountSession cs) {
        if (cs == null) return null;
        return new CountSessionResponse(
                cs.getId(),
                cs.getSessionNumber(),
                cs.getStatus(),
                cs.getSnapshotDate(),
                cs.getCountDate(),
                cs.getValidatedDate(),
                cs.getLines() == null ? List.of() :
                        cs.getLines().stream().map(CountSessionLineResponse::from).collect(Collectors.toList()),
                cs.getTotalQuantityVariance(),
                cs.getTotalCostVariance(),
                cs.getNotes(),
                cs.getCreatedBy(),
                cs.getValidatedBy(),
                cs.getCreatedAt()
        );
    }
}
