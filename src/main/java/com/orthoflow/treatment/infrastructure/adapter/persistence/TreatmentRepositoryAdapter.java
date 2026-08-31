package com.orthoflow.treatment.infrastructure.adapter.persistence;

import com.orthoflow.treatment.domain.model.Treatment;
import com.orthoflow.treatment.domain.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TreatmentRepositoryAdapter implements TreatmentRepository {

    private final TreatmentJpaRepository jpaRepository;

    @Override
    public Treatment save(Treatment treatment) {
        return jpaRepository.save(treatment);
    }

    @Override
    public Optional<Treatment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Treatment> findByCode(String code) {
        return jpaRepository.findByCode(code);
    }

    @Override
    public List<Treatment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
