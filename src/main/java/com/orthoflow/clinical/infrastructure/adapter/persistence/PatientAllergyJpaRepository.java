package com.orthoflow.clinical.infrastructure.adapter.persistence;

import com.orthoflow.clinical.domain.model.PatientAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatientAllergyJpaRepository extends JpaRepository<PatientAllergy, UUID> {
    List<PatientAllergy> findByPatientIdOrderByRecordedAtDesc(UUID patientId);
    List<PatientAllergy> findBySessionIdOrderByRecordedAtAsc(UUID sessionId);
}
