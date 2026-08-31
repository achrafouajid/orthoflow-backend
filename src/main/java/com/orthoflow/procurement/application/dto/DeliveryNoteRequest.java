package com.orthoflow.procurement.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import com.orthoflow.common.web.IdReference;

/**
 * Request DTO for creating a delivery note. Deliberately excludes
 * `supplier` (derived from the purchase order server-side), `status`,
 * `stockMovementsGenerated`, `id`, timestamps — server-owned.
 *
 * `dnNumber` IS accepted from the client, unlike the auto-numbered
 * PO/invoice/vendor-invoice/count-session documents elsewhere in this
 * module — it isn't OrthoFlow's own internal sequence, it's the *supplier's*
 * physical delivery-slip reference (the frontend's placeholder is literally
 * "e.g. BL-99382" — Bon de Livraison), which only the person receiving the
 * goods can know. {@code DeliveryNoteService.createDeliveryNote} falls back
 * to generating one via {@code dn_seq} only when this is left blank, and
 * the DB's `UNIQUE` constraint on `dn_number` is the real backstop against
 * collision either way — so accepting it here is data entry, not the
 * mass-assignment hole this DTO migration was closing elsewhere.
 */
@Getter
@Setter
public class DeliveryNoteRequest {

    @NotNull
    @Valid
    private IdReference purchaseOrder;

    @Size(max = 100)
    private String dnNumber;

    private String notes;

    @NotEmpty
    @Valid
    private List<DeliveryNoteLineRequest> lines;
}
