package com.orthoflow.clinical.infrastructure.adapter.persistence;

import com.orthoflow.clinical.domain.model.ToothStateEvent;
import com.orthoflow.clinical.domain.repository.ToothStateEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ToothStateEventRepositoryAdapter implements ToothStateEventRepository {

    private final ToothStateEventJpaRepository jpaRepository;

    @Override
    public ToothStateEvent save(ToothStateEvent event) {
        return jpaRepository.save(event);
    }

    @Override
    public List<ToothStateEvent> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientIdOrderByOccurredAtDesc(patientId);
    }
}
