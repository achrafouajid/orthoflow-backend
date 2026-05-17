package com.orthoflow.stock.presentation.controller;

import com.orthoflow.stock.application.service.SalesOrderService;
import com.orthoflow.stock.domain.model.SalesOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock/sales-orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @GetMapping
    public ResponseEntity<List<SalesOrder>> getAll() {
        return ResponseEntity.ok(salesOrderService.getAllSalesOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrder> getById(@PathVariable UUID id) {
        return salesOrderService.getSalesOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SalesOrder> create(@RequestBody SalesOrder order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID());
        }
        return ResponseEntity.ok(salesOrderService.createSalesOrder(order));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<SalesOrder> confirm(@PathVariable UUID id, @RequestParam UUID confirmedBy) {
        return ResponseEntity.ok(salesOrderService.confirmSalesOrder(id, confirmedBy));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SalesOrder> cancel(@PathVariable UUID id, @RequestParam UUID cancelledBy) {
        return ResponseEntity.ok(salesOrderService.cancelSalesOrder(id, cancelledBy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        salesOrderService.deleteSalesOrder(id);
        return ResponseEntity.noContent().build();
    }
}
