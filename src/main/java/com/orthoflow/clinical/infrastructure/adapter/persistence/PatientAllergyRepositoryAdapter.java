package com.orthoflow.clinical.infrastructure.adapter.persistence;

import com.orthoflow.clinical.domain.model.PatientAllergy;
import com.orthoflow.clinical.domain.repository.PatientAllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PatientAllergyRepositoryAdapter implements PatientAllergyRepository {

    private final PatientAllergyJpaRepository jpaRepository;

    @Override
    public PatientAllergy save(PatientAllergy allergy) {
        return jpaRepository.save(allergy);
    }

    @Override
    public Optional<PatientAllergy> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PatientAllergy> findByPatient(UUID patientId) {
        return jpaRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
    }

    @Override
    public List<PatientAllergy> findBySession(UUID sessionId) {
        return jpaRepository.findBySessionIdOrderByRecordedAtAsc(sessionId);
    }
}
