package com.orthoflow.inventory.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.inventory.application.dto.StockItemRequest;
import com.orthoflow.inventory.application.dto.StockItemResponse;
import com.orthoflow.inventory.application.dto.StockMovementResponse;
import com.orthoflow.inventory.application.dto.SupplierRequest;
import com.orthoflow.inventory.application.dto.SupplierResponse;
import com.orthoflow.inventory.application.service.StockService;
import com.orthoflow.inventory.domain.model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.orthoflow.inventory.domain.model.MovementType;
import com.orthoflow.inventory.domain.model.SourceType;
import com.orthoflow.inventory.domain.model.StockItemFilter;
import com.orthoflow.inventory.domain.model.StockMovement;
import com.orthoflow.inventory.domain.model.StockMovementFilter;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final CurrentUserProvider currentUserProvider;

    // ---------------- Stock Items Endpoints ----------------

    /**
     * Returns a filtered and sorted list of stock items.
     *
     * @param search   Optional free-text search on name / SKU (case-insensitive).
     * @param category Optional category filter (e.g. CONSUMABLES). Use "ALL" or omit for no filter.
     * @param sortBy   Field to sort by: name, sku, category, currentStock, purchasePrice,
     *                 pricePerUse, createdAt. Defaults to "name".
     * @param sortDir  Sort direction: ASC or DESC. Defaults to "ASC".
     */
    @GetMapping("/items")
    public ResponseEntity<List<StockItemResponse>> getAllStockItems(
            @RequestParam(required = false)            String search,
            @RequestParam(required = false)            String category,
            @RequestParam(defaultValue = "name")       String sortBy,
            @RequestParam(defaultValue = "ASC")        String sortDir) {

        StockItemFilter filter = new StockItemFilter(search, category, sortBy, sortDir);
        return ResponseEntity.ok(stockService.getStockItems(filter).stream()
                .map(StockItemResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/items/low-stock")
    public ResponseEntity<List<StockItemResponse>> getLowStockItems() {
        return ResponseEntity.ok(stockService.getLowStockItems().stream()
                .map(StockItemResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/items/expiring")
    public ResponseEntity<List<StockItemResponse>> getExpiringItems(
            @RequestParam(defaultValue = "30") int daysAhead) {
        return ResponseEntity.ok(stockService.getExpiringItems(daysAhead).stream()
                .map(StockItemResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<StockItemResponse> getStockItemById(@PathVariable UUID id) {
        return stockService.getStockItemById(id)
                .map(item -> ResponseEntity.ok(StockItemResponse.from(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/items/sku/{sku}")
    public ResponseEntity<StockItemResponse> getStockItemBySku(@PathVariable String sku) {
        return stockService.getStockItemBySku(sku)
                .map(item -> ResponseEntity.ok(StockItemResponse.from(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/items")
    public ResponseEntity<StockItemResponse> createStockItem(@Valid @RequestBody StockItemRequest request) {
        return ResponseEntity.ok(StockItemResponse.from(
                stockService.createStockItem(request, currentUserProvider.requireUserId())));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<StockItemResponse> updateStockItem(@PathVariable UUID id, @Valid @RequestBody StockItemRequest request) {
        return ResponseEntity.ok(StockItemResponse.from(stockService.updateStockItem(id, request)));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteStockItem(@PathVariable UUID id) {
        stockService.deleteStockItem(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Suppliers Endpoints ----------------

    @GetMapping("/suppliers")
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        return ResponseEntity.ok(stockService.getAllSuppliers().stream()
                .map(SupplierResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable UUID id) {
        return stockService.getSupplierById(id)
                .map(s -> ResponseEntity.ok(SupplierResponse.from(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/suppliers")
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(SupplierResponse.from(stockService.saveSupplier(request, null)));
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable UUID id, @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(SupplierResponse.from(stockService.saveSupplier(request, id)));
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable UUID id) {
        stockService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Stock Movements Endpoints ----------------

    /**
     * Returns a filtered and sorted audit ledger of stock movements.
     *
     * @param search       Optional free-text search on item name, SKU or source reference.
     * @param movementType Optional type filter (e.g. IN, OUT, ADJUSTMENT). Use "ALL" or omit for none.
     * @param sortBy       Field to sort by: createdAt, movementType, quantity,
     *                     quantityBefore, quantityAfter, sourceType, sourceReference.
     *                     Defaults to "createdAt".
     * @param sortDir      ASC or DESC. Defaults to "DESC" (newest first).
     */
    @GetMapping("/movements")
    public ResponseEntity<org.springframework.data.domain.Page<StockMovementResponse>> getAllMovements(
            @RequestParam(required = false)                 String search,
            @RequestParam(required = false)                 String movementType,
            @RequestParam(defaultValue = "createdAt")       String sortBy,
            @RequestParam(defaultValue = "DESC")            String sortDir,
            @org.springframework.data.web.PageableDefault(size = 200) org.springframework.data.domain.Pageable pageable) {

        StockMovementFilter filter = new StockMovementFilter(search, movementType, sortBy, sortDir);
        return ResponseEntity.ok(stockService.getMovements(filter, pageable).map(StockMovementResponse::from));
    }

    @GetMapping("/items/{id}/movements")
    public ResponseEntity<List<StockMovementResponse>> getMovementsByItem(@PathVariable UUID id) {
        return ResponseEntity.ok(stockService.getMovementsByStockItem(id).stream()
                .map(StockMovementResponse::from).collect(Collectors.toList()));
    }

    @PostMapping("/items/{id}/adjustment")
    public ResponseEntity<StockMovementResponse> adjustStock(
            @PathVariable UUID id,
            @RequestParam BigDecimal quantity,
            @RequestParam String notes) {

        StockMovement movement = stockService.recordMovement(
                id,
                MovementType.ADJUSTMENT,
                quantity,
                SourceType.MANUAL_ADJUSTMENT,
                id,
                "MANUAL",
                notes,
                currentUserProvider.requireUserId()
        );
        return ResponseEntity.ok(StockMovementResponse.from(movement));
    }
}
