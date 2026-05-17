package com.orthoflow.stock.domain.repository;

import com.orthoflow.stock.domain.model.SalesOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalesOrderRepository {
    SalesOrder save(SalesOrder salesOrder);
    Optional<SalesOrder> findById(UUID id);
    Optional<SalesOrder> findBySoNumber(String soNumber);
    List<SalesOrder> findAll();
    void deleteById(UUID id);
}
