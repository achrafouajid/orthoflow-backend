package com.orthoflow.clinical.domain.repository;

import com.orthoflow.clinical.domain.model.DentalChart;

import java.util.Optional;
import java.util.UUID;

public interface DentalChartRepository {
    DentalChart save(DentalChart chart);
    Optional<DentalChart> findByPatientId(UUID patientId);
}
