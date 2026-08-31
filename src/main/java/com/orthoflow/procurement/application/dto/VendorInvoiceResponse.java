package com.orthoflow.procurement.application.dto;

import com.orthoflow.procurement.domain.model.VendorInvoice;
import com.orthoflow.procurement.domain.model.VendorInvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.orthoflow.inventory.application.dto.SupplierResponse;

public record VendorInvoiceResponse(
        UUID id,
        String vendorInvoiceNumber,
        DeliveryNoteResponse deliveryNote,
        SupplierResponse supplier,
        VendorInvoiceStatus status,
        LocalDate invoiceDate,
        BigDecimal invoiceAmount,
        List<VendorInvoiceLineResponse> lines,
        String paymentTerms,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime validatedAt
) {
    public static VendorInvoiceResponse from(VendorInvoice vi) {
        if (vi == null) return null;
        return new VendorInvoiceResponse(
                vi.getId(),
                vi.getVendorInvoiceNumber(),
                DeliveryNoteResponse.from(vi.getDeliveryNote()),
                SupplierResponse.from(vi.getSupplier()),
                vi.getStatus(),
                vi.getInvoiceDate(),
                vi.getInvoiceAmount(),
                vi.getLines() == null ? List.of() :
                        vi.getLines().stream().map(VendorInvoiceLineResponse::from).collect(Collectors.toList()),
                vi.getPaymentTerms(),
                vi.getNotes(),
                vi.getCreatedAt(),
                vi.getValidatedAt()
        );
    }
}
