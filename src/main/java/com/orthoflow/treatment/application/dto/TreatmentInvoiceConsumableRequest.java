package com.orthoflow.treatment.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import com.orthoflow.common.web.IdReference;

/**
 * Deliberately excludes `pricePerUnit` and `totalCost` — those are
 * server-computed from the stock item's current `pricePerUse` on every save
 * (see TreatmentInvoiceService.saveInvoice), never trusted from the client.
 * The previous entity-binding behaviour let a client supply an arbitrary
 * `pricePerUnit`, which fed directly into TreatmentInvoice.calculateTotal()
 * — a price-tampering vector (audit I.5 / V.6). `modified` is likewise
 * excluded; it is derived automatically from defaultQuantity/actualQuantity
 * by the entity's own lifecycle callbacks.
 */
@Getter
@Setter
public class TreatmentInvoiceConsumableRequest {

    @NotNull
    @Valid
    private IdReference stockItem;

    @NotNull
    @PositiveOrZero
    private BigDecimal actualQuantity;

    /** Optional — falls back to actualQuantity for newly-added lines. */
    @PositiveOrZero
    private BigDecimal defaultQuantity;
}
