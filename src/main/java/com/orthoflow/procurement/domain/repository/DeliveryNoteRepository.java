package com.orthoflow.procurement.domain.repository;

import com.orthoflow.procurement.domain.model.DeliveryNote;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryNoteRepository {
    DeliveryNote save(DeliveryNote deliveryNote);
    Optional<DeliveryNote> findById(UUID id);
    Optional<DeliveryNote> findByDnNumber(String dnNumber);
    List<DeliveryNote> findAll();
    /** RECEIVED delivery notes with no non-cancelled vendor invoice referencing them yet (BR03 GRNI). */
    List<DeliveryNote> findOpenGrni();
    void deleteById(UUID id);
}
