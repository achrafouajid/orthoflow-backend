package com.orthoflow.procurement.domain.repository;

import com.orthoflow.procurement.domain.model.VendorInvoice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorInvoiceRepository {
    VendorInvoice save(VendorInvoice vendorInvoice);
    Optional<VendorInvoice> findById(UUID id);
    List<VendorInvoice> findAll();
    /** Non-cancelled vendor invoices referencing the given delivery note — used both for the
     *  create-time "already invoiced" guard and to compute the open-GRNI list. */
    List<VendorInvoice> findActiveByDeliveryNoteId(UUID deliveryNoteId);
    void deleteById(UUID id);
}
