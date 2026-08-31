package com.orthoflow.procurement.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.procurement.application.dto.PurchaseOrderRequest;
import com.orthoflow.procurement.application.dto.PurchaseOrderResponse;
import com.orthoflow.procurement.application.service.PurchaseOrderService;
import com.orthoflow.procurement.domain.model.POStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stock/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>> getAll() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders().stream()
                .map(PurchaseOrderResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponse> getById(@PathVariable UUID id) {
        return purchaseOrderService.getPurchaseOrderById(id)
                .map(po -> ResponseEntity.ok(PurchaseOrderResponse.from(po)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<PurchaseOrderResponse> getByNumber(@PathVariable String number) {
        return purchaseOrderService.getPurchaseOrderByNumber(number)
                .map(po -> ResponseEntity.ok(PurchaseOrderResponse.from(po)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> create(@Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(
                purchaseOrderService.createPurchaseOrder(request, currentUserProvider.requireUserId())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponse> update(@PathVariable UUID id, @Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.updatePurchaseOrder(id, request)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrderResponse> updateStatus(@PathVariable UUID id, @RequestParam POStatus status) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.updatePurchaseOrderStatus(id, status)));
    }

    /** Semantic alias for the frontend's explicit "confirm" action: DRAFT → SENT. */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<PurchaseOrderResponse> confirm(@PathVariable UUID id) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.confirmPurchaseOrder(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }
}
