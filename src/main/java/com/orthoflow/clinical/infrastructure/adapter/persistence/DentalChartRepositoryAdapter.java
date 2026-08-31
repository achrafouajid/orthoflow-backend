package com.orthoflow.clinical.infrastructure.adapter.persistence;

import com.orthoflow.clinical.domain.model.DentalChart;
import com.orthoflow.clinical.domain.repository.DentalChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DentalChartRepositoryAdapter implements DentalChartRepository {

    private final DentalChartJpaRepository jpaRepository;

    @Override
    public DentalChart save(DentalChart chart) {
        return jpaRepository.save(chart);
    }

    @Override
    public Optional<DentalChart> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId);
    }
}
