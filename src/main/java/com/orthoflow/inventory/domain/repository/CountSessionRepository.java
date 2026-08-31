package com.orthoflow.inventory.domain.repository;

import com.orthoflow.inventory.domain.model.CountSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CountSessionRepository {
    CountSession save(CountSession countSession);
    Optional<CountSession> findById(UUID id);
    List<CountSession> findAll();
    /** Most recently validated session, if any — used by the inventory KPI variance calc. */
    Optional<CountSession> findTopByStatusValidatedOrderByValidatedDateDesc();
    void deleteById(UUID id);
}
