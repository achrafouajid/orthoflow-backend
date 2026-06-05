package com.orthoflow.stock.domain.repository;

import com.orthoflow.stock.domain.model.PatientTreatment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientTreatmentRepository {
    PatientTreatment save(PatientTreatment patientTreatment);
    Optional<PatientTreatment> findById(UUID id);
    List<PatientTreatment> findByPatientId(UUID patientId);
    List<PatientTreatment> findAll();
    void deleteById(UUID id);
}
