package com.orthoflow.scheduling.infrastructure.adapter.persistence;

import com.orthoflow.scheduling.domain.model.Chair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChairJpaRepository extends JpaRepository<Chair, UUID> {
    List<Chair> findByActiveTrue();
}
