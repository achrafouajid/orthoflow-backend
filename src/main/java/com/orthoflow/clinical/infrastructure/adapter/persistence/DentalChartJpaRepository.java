package com.orthoflow.clinical.infrastructure.adapter.persistence;

import com.orthoflow.clinical.domain.model.DentalChart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DentalChartJpaRepository extends JpaRepository<DentalChart, UUID> {
    Optional<DentalChart> findByPatientId(UUID patientId);
}
