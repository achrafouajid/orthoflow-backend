package com.orthoflow.common.web;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Minimal reference DTO for a foreign-key relationship sent by the frontend
 * as a nested object (e.g. {@code "supplier": {"id": "...", "name": "...",
 * ...}}, {@code "stockItem": {"id": "..."}}). Only {@code id} is bound —
 * every other property on the nested JSON object is ignored — and the
 * referenced entity is always re-resolved server-side via its repository.
 *
 * Binding the full nested entity (the previous behaviour across
 * PurchaseOrder/SalesOrder/Treatment/TreatmentInvoice/DeliveryNote) let a
 * client's own copy of a related record — however stale or forged — flow
 * straight into a JPA relationship and get persisted through cascade (audit
 * I.5 / V.6). Kept as a single reusable type, rather than one bespoke
 * "XxxRef" per relationship, since the trust boundary is identical in every
 * case: accept an id, verify it server-side, discard the rest.
 */
@Getter
@Setter
public class IdReference {

    @NotNull
    private UUID id;
}
