package com.orthoflow.inventory.infrastructure.adapter.persistence;

import com.orthoflow.inventory.domain.model.CountSession;
import com.orthoflow.inventory.domain.repository.CountSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CountSessionRepositoryAdapter implements CountSessionRepository {

    private final CountSessionJpaRepository jpaRepository;

    @Override
    public CountSession save(CountSession countSession) {
        return jpaRepository.save(countSession);
    }

    @Override
    public Optional<CountSession> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CountSession> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<CountSession> findTopByStatusValidatedOrderByValidatedDateDesc() {
        return jpaRepository.findTopByStatusValidatedOrderByValidatedDateDesc();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
