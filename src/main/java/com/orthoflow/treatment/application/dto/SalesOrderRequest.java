package com.orthoflow.treatment.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import com.orthoflow.common.web.IdReference;

/**
 * Request DTO for creating a sales order. Deliberately excludes `status`,
 * `totalAmount`, `soNumber`, `id`, `createdAt` — server-owned. Binding the
 * entity directly (the previous behaviour) let a client set `totalAmount`
 * or `status` directly, bypassing `calculateTotal()` and the DRAFT-only
 * lifecycle (see audit I.5 / V.6).
 */
@Getter
@Setter
public class SalesOrderRequest {

    @NotNull
    @Valid
    private IdReference patient;

    private String notes;

    @NotEmpty
    @Valid
    private List<SalesOrderLineRequest> lines;
}
