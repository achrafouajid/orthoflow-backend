package com.orthoflow.treatment.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.treatment.application.dto.SalesOrderRequest;
import com.orthoflow.treatment.application.dto.SalesOrderResponse;
import com.orthoflow.treatment.application.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stock/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<SalesOrderResponse>> getAll() {
        return ResponseEntity.ok(salesOrderService.getAllSalesOrders().stream()
                .map(SalesOrderResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderResponse> getById(@PathVariable UUID id) {
        return salesOrderService.getSalesOrderById(id)
                .map(so -> ResponseEntity.ok(SalesOrderResponse.from(so)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SalesOrderResponse> create(@Valid @RequestBody SalesOrderRequest request) {
        return ResponseEntity.ok(SalesOrderResponse.from(salesOrderService.createSalesOrder(request)));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<SalesOrderResponse> confirm(@PathVariable UUID id) {
        return ResponseEntity.ok(SalesOrderResponse.from(
                salesOrderService.confirmSalesOrder(id, currentUserProvider.requireUserId())));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SalesOrderResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(SalesOrderResponse.from(
                salesOrderService.cancelSalesOrder(id, currentUserProvider.requireUserId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        salesOrderService.deleteSalesOrder(id);
        return ResponseEntity.noContent().build();
    }
}
