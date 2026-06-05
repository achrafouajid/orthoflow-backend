package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.PatientTreatment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PatientTreatmentJpaRepository extends JpaRepository<PatientTreatment, UUID> {
    List<PatientTreatment> findByPatientId(UUID patientId);
}
