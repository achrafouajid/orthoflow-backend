package com.orthoflow.stock.presentation.controller;

import com.orthoflow.stock.application.service.StockService;
import com.orthoflow.stock.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

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
    public ResponseEntity<List<StockItem>> getAllStockItems(
            @RequestParam(required = false)            String search,
            @RequestParam(required = false)            String category,
            @RequestParam(defaultValue = "name")       String sortBy,
            @RequestParam(defaultValue = "ASC")        String sortDir) {

        StockItemFilter filter = new StockItemFilter(search, category, sortBy, sortDir);
        return ResponseEntity.ok(stockService.getStockItems(filter));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<StockItem> getStockItemById(@PathVariable UUID id) {
        return stockService.getStockItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/items/sku/{sku}")
    public ResponseEntity<StockItem> getStockItemBySku(@PathVariable String sku) {
        return stockService.getStockItemBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/items")
    public ResponseEntity<StockItem> createStockItem(@RequestBody StockItem item) {
        if (item.getId() == null) {
            item.setId(UUID.randomUUID());
        }
        return ResponseEntity.ok(stockService.saveStockItem(item));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<StockItem> updateStockItem(@PathVariable UUID id, @RequestBody StockItem item) {
        item.setId(id);
        return ResponseEntity.ok(stockService.saveStockItem(item));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteStockItem(@PathVariable UUID id) {
        stockService.deleteStockItem(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Suppliers Endpoints ----------------

    @GetMapping("/suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(stockService.getAllSuppliers());
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable UUID id) {
        return stockService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        if (supplier.getId() == null) {
            supplier.setId(UUID.randomUUID());
        }
        return ResponseEntity.ok(stockService.saveSupplier(supplier));
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable UUID id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        return ResponseEntity.ok(stockService.saveSupplier(supplier));
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
    public ResponseEntity<List<StockMovement>> getAllMovements(
            @RequestParam(required = false)                 String search,
            @RequestParam(required = false)                 String movementType,
            @RequestParam(defaultValue = "createdAt")       String sortBy,
            @RequestParam(defaultValue = "DESC")            String sortDir) {

        StockMovementFilter filter = new StockMovementFilter(search, movementType, sortBy, sortDir);
        return ResponseEntity.ok(stockService.getMovements(filter));
    }

    @GetMapping("/items/{id}/movements")
    public ResponseEntity<List<StockMovement>> getMovementsByItem(@PathVariable UUID id) {
        return ResponseEntity.ok(stockService.getMovementsByStockItem(id));
    }

    @PostMapping("/items/{id}/adjustment")
    public ResponseEntity<StockMovement> adjustStock(
            @PathVariable UUID id,
            @RequestParam BigDecimal quantity,
            @RequestParam String notes,
            @RequestParam(required = false) UUID createdBy) {
        
        StockMovement movement = stockService.recordMovement(
                id,
                MovementType.ADJUSTMENT,
                quantity,
                SourceType.MANUAL_ADJUSTMENT,
                id,
                "MANUAL",
                notes,
                createdBy != null ? createdBy : UUID.randomUUID()
        );
        return ResponseEntity.ok(movement);
    }
}
