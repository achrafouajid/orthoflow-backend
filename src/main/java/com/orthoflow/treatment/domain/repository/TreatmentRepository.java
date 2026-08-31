package com.orthoflow.treatment.domain.repository;

import com.orthoflow.treatment.domain.model.Treatment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TreatmentRepository {
    Treatment save(Treatment treatment);
    Optional<Treatment> findById(UUID id);
    Optional<Treatment> findByCode(String code);
    List<Treatment> findAll();
    void deleteById(UUID id);
}
