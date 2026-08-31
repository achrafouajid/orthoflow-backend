package com.orthoflow.procurement.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.orthoflow.common.web.IdReference;

/**
 * `stockItem` is deliberately absent — it is always derived server-side from
 * the referenced PO line (see DeliveryNoteService.createDeliveryNote), never
 * trusted from the client.
 */
@Getter
@Setter
public class DeliveryNoteLineRequest {

    @NotNull
    @Valid
    private IdReference poLine;

    @NotNull
    @PositiveOrZero
    private BigDecimal quantityExpected;

    @NotNull
    @PositiveOrZero
    private BigDecimal quantityReceived;

    private String batchNumber;

    private LocalDate expiryDate;
}
