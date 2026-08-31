package com.orthoflow.procurement.infrastructure.adapter.persistence;

import com.orthoflow.procurement.domain.model.VendorInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface VendorInvoiceJpaRepository extends JpaRepository<VendorInvoice, UUID> {

    @Query("SELECT vi FROM VendorInvoice vi WHERE vi.deliveryNote.id = :deliveryNoteId " +
            "AND vi.status <> com.orthoflow.procurement.domain.model.VendorInvoiceStatus.CANCELLED")
    List<VendorInvoice> findActiveByDeliveryNoteId(@Param("deliveryNoteId") UUID deliveryNoteId);
}
