package com.orthoflow.stock.presentation.controller;

import com.orthoflow.stock.application.service.PurchaseOrderService;
import com.orthoflow.stock.domain.model.POStatus;
import com.orthoflow.stock.domain.model.PurchaseOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock/purchase-orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAll() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getById(@PathVariable UUID id) {
        return purchaseOrderService.getPurchaseOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<PurchaseOrder> getByNumber(@PathVariable String number) {
        return purchaseOrderService.getPurchaseOrderByNumber(number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> create(@RequestBody PurchaseOrder order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID());
        }
        return ResponseEntity.ok(purchaseOrderService.createPurchaseOrder(order));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrder> updateStatus(@PathVariable UUID id, @RequestParam POStatus status) {
        return ResponseEntity.ok(purchaseOrderService.updatePurchaseOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }
}
