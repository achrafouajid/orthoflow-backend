package com.orthoflow.treatment.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.orthoflow.common.web.IdReference;

/**
 * Request DTO for the treatment-invoice upsert endpoint (POST
 * /stock/treatment-invoices — used for both "save draft" and "edit draft").
 * There is no path {id}; the frontend embeds `id` in the body when editing
 * an existing DRAFT, so — unlike the other request DTOs in this package —
 * `id` is intentionally part of this one (see TreatmentInvoiceService for
 * the create-vs-update branch).
 *
 * Deliberately excludes `status`, `stockMovementsGenerated`, `createdBy`,
 * `invoiceNumber`, `finalizedAt`, `pricePerUnit`/`totalCost` on lines, and
 * `consumablesCost`/`subtotal`/`discountAmount`/`total` — all server-owned.
 * Binding the entity directly (the previous behaviour) let a client set
 * `status: "FINALIZED"` on this endpoint directly, skipping the stock
 * validation/deduction and audit trail that `/finalize` performs (see audit
 * V.6, and I.5 for the equivalent mass-assignment risk here).
 */
@Getter
@Setter
public class TreatmentInvoiceRequest {

    /** Null on create; the existing invoice id when editing a DRAFT. */
    private UUID id;

    @NotNull
    @Valid
    private IdReference patient;

    @NotNull
    @Valid
    private IdReference treatment;

    private LocalDate sessionDate;

    @NotNull
    @PositiveOrZero
    private BigDecimal treatmentPrice;

    private String notes;

    @Valid
    private List<TreatmentInvoiceConsumableRequest> consumablesUsed;

    @Valid
    private List<InvoiceDiscountRequest> discounts;
}
