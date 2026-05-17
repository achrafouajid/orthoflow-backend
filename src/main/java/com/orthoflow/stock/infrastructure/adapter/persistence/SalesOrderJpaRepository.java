package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SalesOrderJpaRepository extends JpaRepository<SalesOrder, UUID> {
    Optional<SalesOrder> findBySoNumber(String soNumber);
}
