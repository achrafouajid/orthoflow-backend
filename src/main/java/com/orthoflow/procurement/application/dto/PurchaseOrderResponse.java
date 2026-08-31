package com.orthoflow.procurement.application.dto;

import com.orthoflow.procurement.domain.model.POStatus;
import com.orthoflow.procurement.domain.model.PurchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.orthoflow.inventory.application.dto.SupplierResponse;

public record PurchaseOrderResponse(
        UUID id,
        String poNumber,
        SupplierResponse supplier,
        POStatus status,
        List<PurchaseOrderLineResponse> lines,
        BigDecimal totalAmount,
        LocalDate orderDate,
        LocalDate expectedDeliveryDate,
        String notes
) {
    public static PurchaseOrderResponse from(PurchaseOrder po) {
        if (po == null) return null;
        return new PurchaseOrderResponse(
                po.getId(),
                po.getPoNumber(),
                SupplierResponse.from(po.getSupplier()),
                po.getStatus(),
                po.getLines() == null ? List.of() :
                        po.getLines().stream().map(PurchaseOrderLineResponse::from).collect(Collectors.toList()),
                po.getTotalAmount(),
                po.getOrderDate(),
                po.getExpectedDeliveryDate(),
                po.getNotes()
        );
    }
}
