package com.orthoflow.inventory.infrastructure.adapter.persistence;

import com.orthoflow.inventory.domain.model.CountSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CountSessionJpaRepository extends JpaRepository<CountSession, UUID> {

    @Query("SELECT cs FROM CountSession cs WHERE cs.status = com.orthoflow.inventory.domain.model.CountSessionStatus.VALIDATED " +
            "ORDER BY cs.validatedDate DESC")
    List<CountSession> findValidatedOrderByValidatedDateDesc();

    default Optional<CountSession> findTopByStatusValidatedOrderByValidatedDateDesc() {
        List<CountSession> results = findValidatedOrderByValidatedDateDesc();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
