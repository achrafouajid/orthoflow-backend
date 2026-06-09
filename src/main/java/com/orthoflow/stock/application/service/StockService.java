package com.orthoflow.stock.application.service;

import com.orthoflow.stock.domain.model.*;
import com.orthoflow.stock.domain.repository.StockItemRepository;
import com.orthoflow.stock.domain.repository.StockMovementRepository;
import com.orthoflow.stock.domain.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Filter imports — domain value objects (DIP: service depends on domain, not infrastructure)

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final SupplierRepository supplierRepository;

    public List<StockItem> getAllStockItems() {
        return stockItemRepository.findAll();
    }

    /**
     * Returns stock items matching the given filter (search, category) with server-side sort.
     * Delegates directly to the repository — service stays thin.
     */
    public List<StockItem> getStockItems(StockItemFilter filter) {
        return stockItemRepository.findAll(filter);
    }

    public Optional<StockItem> getStockItemById(UUID id) {
        return stockItemRepository.findById(id);
    }

    public Optional<StockItem> getStockItemBySku(String sku) {
        return stockItemRepository.findBySku(sku);
    }

    @Transactional
    public StockItem saveStockItem(StockItem item) {
        if (item.getSupplier() != null && item.getSupplier().getId() != null) {
            Supplier supplier = supplierRepository.findById(item.getSupplier().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + item.getSupplier().getId()));
            item.setSupplier(supplier);
        }
        item.computePricePerUse();
        return stockItemRepository.save(item);
    }

    @Transactional
    public void deleteStockItem(UUID id) {
        stockItemRepository.deleteById(id);
    }

    // ---------------- Supplier Management ----------------
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(UUID id) {
        return supplierRepository.findById(id);
    }

    @Transactional
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteSupplier(UUID id) {
        supplierRepository.deleteById(id);
    }

    // ---------------- Stock Movement Logging & Logic ----------------
    @Transactional
    public StockMovement recordMovement(UUID stockItemId, MovementType type, BigDecimal quantity, 
                                        SourceType sourceType, UUID sourceId, String sourceReference, 
                                        String notes, UUID createdBy) {
        
        StockItem stockItem = stockItemRepository.findById(stockItemId)
                .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + stockItemId));

        BigDecimal quantityBefore = stockItem.getCurrentStock() != null ? stockItem.getCurrentStock() : BigDecimal.ZERO;
        BigDecimal quantityAfter;

        switch (type) {
            case IN:
            case RETURN:
                quantityAfter = quantityBefore.add(quantity);
                break;
            case OUT:
            case WRITE_OFF:
                quantityAfter = quantityBefore.subtract(quantity);
                break;
            case ADJUSTMENT:
                // For manual adjustment, quantity represents the signed delta (positive to add, negative to subtract)
                quantityAfter = quantityBefore.add(quantity);
                break;
            default:
                throw new IllegalArgumentException("Unsupported movement type: " + type);
        }

        stockItem.setCurrentStock(quantityAfter);
        stockItemRepository.save(stockItem);

        StockMovement movement = StockMovement.builder()
                .stockItem(stockItem)
                .movementType(type)
                .quantity(quantity)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .sourceReference(sourceReference)
                .notes(notes)
                .createdBy(createdBy != null ? createdBy : UUID.randomUUID())
                .build();

        StockMovement savedMovement = stockMovementRepository.save(movement);

        // Low stock alerts
        if (quantityAfter.compareTo(stockItem.getMinimumStock()) < 0) {
            log.warn("ALERT: Stock item '{}' (SKU: {}) is below minimum stock! Current: {}, Minimum: {}",
                    stockItem.getName(), stockItem.getSku(), quantityAfter, stockItem.getMinimumStock());
        }

        return savedMovement;
    }

    public List<StockMovement> getMovementsByStockItem(UUID stockItemId) {
        return stockMovementRepository.findByStockItemId(stockItemId);
    }

    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAll();
    }

    /**
     * Returns stock movements matching the given filter (search, type) with server-side sort.
     */
    public List<StockMovement> getMovements(StockMovementFilter filter) {
        return stockMovementRepository.findAll(filter);
    }
}
