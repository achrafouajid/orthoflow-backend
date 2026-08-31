package com.orthoflow.procurement.infrastructure.adapter.persistence;

import com.orthoflow.procurement.domain.model.DeliveryNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.orthoflow.procurement.domain.model.VendorInvoice;

public interface DeliveryNoteJpaRepository extends JpaRepository<DeliveryNote, UUID> {
    Optional<DeliveryNote> findByDnNumber(String dnNumber);

    @Query("SELECT dn FROM DeliveryNote dn WHERE dn.status = com.orthoflow.procurement.domain.model.DNStatus.RECEIVED " +
            "AND NOT EXISTS (SELECT 1 FROM VendorInvoice vi WHERE vi.deliveryNote = dn " +
            "AND vi.status <> com.orthoflow.procurement.domain.model.VendorInvoiceStatus.CANCELLED)")
    List<DeliveryNote> findOpenGrni();
}
