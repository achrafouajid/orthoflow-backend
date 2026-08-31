package com.orthoflow.treatment.application.dto;

import com.orthoflow.patient.application.port.PatientSummary;
import com.orthoflow.treatment.domain.model.SOStatus;
import com.orthoflow.treatment.domain.model.SalesOrder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record SalesOrderResponse(
        UUID id,
        String soNumber,
        PatientSummary patient,
        SOStatus status,
        List<SalesOrderLineResponse> lines,
        BigDecimal totalAmount,
        String notes,
        OffsetDateTime createdAt
) {
    public static SalesOrderResponse from(SalesOrder so) {
        if (so == null) return null;
        return new SalesOrderResponse(
                so.getId(),
                so.getSoNumber(),
                so.getPatient(),
                so.getStatus(),
                so.getLines() == null ? List.of() :
                        so.getLines().stream().map(SalesOrderLineResponse::from).collect(Collectors.toList()),
                so.getTotalAmount(),
                so.getNotes(),
                so.getCreatedAt()
        );
    }
}
