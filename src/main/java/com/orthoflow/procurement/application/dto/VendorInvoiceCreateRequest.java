package com.orthoflow.procurement.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Payload for POST /stock/vendor-invoices. Matches the flat shape the
 * frontend actually sends from purchase-orders.component.ts::registerVendorInvoice()
 * — a delivery note id plus the invoice's own fields, not a nested
 * `deliveryNote`/`supplier` object. `supplier`, `status`, `lines`,
 * `vendorInvoiceNumber` (when omitted) and the audit columns are all
 * server-derived — never accepted from the client.
 */
@Getter
@Setter
public class VendorInvoiceCreateRequest {

    @NotNull
    private UUID deliveryNoteId;

    /** Optional — server generates one via vendor_invoice_seq when absent. */
    private String invoiceNumber;

    @NotNull
    private LocalDate invoiceDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal invoiceAmount;

    private String paymentTerms;

    private String notes;
}
