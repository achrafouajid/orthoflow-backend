package com.orthoflow.procurement.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import com.orthoflow.common.web.IdReference;

/**
 * Request DTO for creating/updating a purchase order. Deliberately excludes
 * `totalAmount`, `status`, `createdBy`, `poNumber`, `id`, timestamps — those
 * are server-owned. Binding the entity directly (the previous behaviour) let
 * a client set `totalAmount` or `status` directly on create/update,
 * bypassing `calculateTotal()` and the DRAFT-only lifecycle (see audit I.5 /
 * V.6). `createdBy` now comes from `CurrentUserProvider`, never the client —
 * previously it was never set at all despite the column being NOT NULL.
 */
@Getter
@Setter
public class PurchaseOrderRequest {

    @NotNull
    @Valid
    private IdReference supplier;

    /** Optional — defaults to today if omitted, matching prior behaviour. */
    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;

    private String notes;

    @NotEmpty
    @Valid
    private List<PurchaseOrderLineRequest> lines;
}
