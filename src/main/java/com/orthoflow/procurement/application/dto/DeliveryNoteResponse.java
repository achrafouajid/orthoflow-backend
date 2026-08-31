package com.orthoflow.procurement.application.dto;

import com.orthoflow.procurement.domain.model.DNStatus;
import com.orthoflow.procurement.domain.model.DeliveryNote;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.orthoflow.inventory.application.dto.SupplierResponse;

public record DeliveryNoteResponse(
        UUID id,
        String dnNumber,
        PurchaseOrderResponse purchaseOrder,
        SupplierResponse supplier,
        LocalDate receivedDate,
        UUID receivedBy,
        DNStatus status,
        List<DeliveryNoteLineResponse> lines,
        boolean stockMovementsGenerated,
        String notes
) {
    public static DeliveryNoteResponse from(DeliveryNote dn) {
        if (dn == null) return null;
        return new DeliveryNoteResponse(
                dn.getId(),
                dn.getDnNumber(),
                PurchaseOrderResponse.from(dn.getPurchaseOrder()),
                SupplierResponse.from(dn.getSupplier()),
                dn.getReceivedDate(),
                dn.getReceivedBy(),
                dn.getStatus(),
                dn.getLines() == null ? List.of() :
                        dn.getLines().stream().map(DeliveryNoteLineResponse::from).collect(Collectors.toList()),
                dn.isStockMovementsGenerated(),
                dn.getNotes()
        );
    }
}
