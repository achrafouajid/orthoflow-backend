package com.orthoflow.stock.domain.repository;

import com.orthoflow.stock.domain.model.DeliveryNote;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryNoteRepository {
    DeliveryNote save(DeliveryNote deliveryNote);
    Optional<DeliveryNote> findById(UUID id);
    Optional<DeliveryNote> findByDnNumber(String dnNumber);
    List<DeliveryNote> findAll();
    void deleteById(UUID id);
}
