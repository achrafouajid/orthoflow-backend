package com.orthoflow.procurement.application.dto;

import com.orthoflow.procurement.domain.model.VendorInvoiceLine;

import java.math.BigDecimal;
import java.util.UUID;
import com.orthoflow.inventory.application.dto.StockItemResponse;

public record VendorInvoiceLineResponse(
        UUID id,
        StockItemResponse stockItem,
        BigDecimal quantityInvoiced,
        BigDecimal unitPrice,
        BigDecimal taxRate,
        BigDecimal lineTotal
) {
    public static VendorInvoiceLineResponse from(VendorInvoiceLine line) {
        if (line == null) return null;
        return new VendorInvoiceLineResponse(
                line.getId(),
                StockItemResponse.from(line.getStockItem()),
                line.getQuantityInvoiced(),
                line.getUnitPrice(),
                line.getTaxRate(),
                line.getLineTotal()
        );
    }
}
