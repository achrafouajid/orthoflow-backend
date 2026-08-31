package com.orthoflow.clinical.domain.repository;

import com.orthoflow.clinical.domain.model.PatientAllergy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientAllergyRepository {
    PatientAllergy save(PatientAllergy allergy);
    Optional<PatientAllergy> findById(UUID id);
    List<PatientAllergy> findByPatient(UUID patientId);
    List<PatientAllergy> findBySession(UUID sessionId);
}
