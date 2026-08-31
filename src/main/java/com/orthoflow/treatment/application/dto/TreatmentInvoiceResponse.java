package com.orthoflow.treatment.application.dto;

import com.orthoflow.patient.application.port.PatientSummary;
import com.orthoflow.treatment.domain.model.TreatmentInvoice;
import com.orthoflow.treatment.domain.model.TreatmentInvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record TreatmentInvoiceResponse(
        UUID id,
        String invoiceNumber,
        PatientSummary patient,
        TreatmentResponse treatment,
        LocalDate sessionDate,
        TreatmentInvoiceStatus status,
        List<TreatmentInvoiceConsumableResponse> consumablesUsed,
        List<InvoiceDiscountResponse> discounts,
        BigDecimal treatmentPrice,
        BigDecimal consumablesCost,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal total,
        boolean stockMovementsGenerated,
        UUID billingInvoiceId,
        String notes,
        String createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime finalizedAt
) {
    public static TreatmentInvoiceResponse from(TreatmentInvoice ti) {
        if (ti == null) return null;
        return new TreatmentInvoiceResponse(
                ti.getId(),
                ti.getInvoiceNumber(),
                ti.getPatient(),
                TreatmentResponse.shallow(ti.getTreatment()),
                ti.getSessionDate(),
                ti.getStatus(),
                ti.getConsumablesUsed() == null ? List.of() :
                        ti.getConsumablesUsed().stream().map(TreatmentInvoiceConsumableResponse::from).collect(Collectors.toList()),
                ti.getDiscounts() == null ? List.of() :
                        ti.getDiscounts().stream().map(InvoiceDiscountResponse::from).collect(Collectors.toList()),
                ti.getTreatmentPrice(),
                ti.getConsumablesCost(),
                ti.getSubtotal(),
                ti.getDiscountAmount(),
                ti.getTotal(),
                ti.isStockMovementsGenerated(),
                ti.getBillingInvoiceId(),
                ti.getNotes(),
                ti.getCreatedBy() == null ? null : ti.getCreatedBy().toString(),
                ti.getCreatedAt(),
                ti.getFinalizedAt()
        );
    }
}
