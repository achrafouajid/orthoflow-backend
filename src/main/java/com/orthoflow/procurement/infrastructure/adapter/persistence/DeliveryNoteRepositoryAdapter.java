package com.orthoflow.procurement.infrastructure.adapter.persistence;

import com.orthoflow.procurement.domain.model.DeliveryNote;
import com.orthoflow.procurement.domain.repository.DeliveryNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryNoteRepositoryAdapter implements DeliveryNoteRepository {

    private final DeliveryNoteJpaRepository jpaRepository;

    @Override
    public DeliveryNote save(DeliveryNote deliveryNote) {
        return jpaRepository.save(deliveryNote);
    }

    @Override
    public Optional<DeliveryNote> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<DeliveryNote> findByDnNumber(String dnNumber) {
        return jpaRepository.findByDnNumber(dnNumber);
    }

    @Override
    public List<DeliveryNote> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<DeliveryNote> findOpenGrni() {
        return jpaRepository.findOpenGrni();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
