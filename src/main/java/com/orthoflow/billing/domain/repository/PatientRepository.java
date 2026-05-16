package com.orthoflow.billing.domain.repository;

import com.orthoflow.billing.domain.model.Patient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(UUID id);
    List<Patient> findAll();
    void deleteById(UUID id);
}
