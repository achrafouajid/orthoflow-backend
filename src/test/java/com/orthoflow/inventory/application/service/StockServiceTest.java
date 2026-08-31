package com.orthoflow.inventory.application.service;

import com.orthoflow.inventory.domain.model.*;
import com.orthoflow.inventory.domain.repository.StockItemRepository;
import com.orthoflow.inventory.domain.repository.StockMovementRepository;
import com.orthoflow.inventory.domain.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Protects the stock-ledger invariants audit II.3/II.6 called out: a
 * movement can never be attributed to a fabricated/null actor, and stock can
 * never go negative regardless of movement type.
 */
@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private SupplierRepository supplierRepository;

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService(stockItemRepository, stockMovementRepository, supplierRepository);
    }

    private StockItem itemWithStock(BigDecimal currentStock) {
        return StockItem.builder()
                .id(UUID.randomUUID())
                .name("Brackets")
                .sku("BR-001")
                .currentStock(currentStock)
                .minimumStock(BigDecimal.TEN)
                .build();
    }

    @Test
    void recordMovement_outboundExceedingStock_isRejectedNotAllowedNegative() {
        StockItem item = itemWithStock(BigDecimal.valueOf(2));
        when(stockItemRepository.findByIdForUpdate(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> stockService.recordMovement(
                item.getId(), MovementType.OUT, BigDecimal.valueOf(10),
                SourceType.MANUAL_ADJUSTMENT, item.getId(), "REF", "notes", UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock");

        verify(stockMovementRepository, never()).save(any());
        assertThat(item.getCurrentStock()).isEqualByComparingTo("2");
    }

    @Test
    void recordMovement_inboundIncreasesStock() {
        StockItem item = itemWithStock(BigDecimal.valueOf(5));
        when(stockItemRepository.findByIdForUpdate(item.getId())).thenReturn(Optional.of(item));
        when(stockItemRepository.save(any(StockItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        StockMovement movement = stockService.recordMovement(
                item.getId(), MovementType.IN, BigDecimal.valueOf(20),
                SourceType.DELIVERY_NOTE, item.getId(), "PO-1", "restock", UUID.randomUUID());

        assertThat(item.getCurrentStock()).isEqualByComparingTo("25");
        assertThat(movement.getQuantityBefore()).isEqualByComparingTo("5");
        assertThat(movement.getQuantityAfter()).isEqualByComparingTo("25");
    }

    @Test
    void recordMovement_adjustmentBelowZero_isRejected() {
        StockItem item = itemWithStock(BigDecimal.valueOf(3));
        when(stockItemRepository.findByIdForUpdate(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> stockService.recordMovement(
                item.getId(), MovementType.ADJUSTMENT, BigDecimal.valueOf(-5),
                SourceType.MANUAL_ADJUSTMENT, item.getId(), "REF", "correction", UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative stock");
    }

    @Test
    void recordMovement_everyMovementRequiresANonNullActor() {
        // audit II.3: a fabricated/random UUID (or null) as createdBy made
        // the audit trail worthless. The service must refuse to record a
        // movement without a real actor rather than silently inventing one.
        StockItem item = itemWithStock(BigDecimal.valueOf(5));
        when(stockItemRepository.findByIdForUpdate(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> stockService.recordMovement(
                item.getId(), MovementType.IN, BigDecimal.valueOf(1),
                SourceType.MANUAL_ADJUSTMENT, item.getId(), "REF", "notes", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void recordMovement_createsAnAuditTrailEntryWithBeforeAndAfterQuantities() {
        StockItem item = itemWithStock(BigDecimal.valueOf(10));
        when(stockItemRepository.findByIdForUpdate(item.getId())).thenReturn(Optional.of(item));
        when(stockItemRepository.save(any(StockItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));
        UUID actor = UUID.randomUUID();

        StockMovement movement = stockService.recordMovement(
                item.getId(), MovementType.OUT, BigDecimal.valueOf(4),
                SourceType.PATIENT_TREATMENT, item.getId(), "PT-1", "consumed", actor);

        assertThat(movement.getCreatedBy()).isEqualTo(actor);
        assertThat(movement.getQuantityBefore()).isEqualByComparingTo("10");
        assertThat(movement.getQuantityAfter()).isEqualByComparingTo("6");
        assertThat(movement.getSourceReference()).isEqualTo("PT-1");
    }

    @Test
    void computePricePerUse_neverProducesANegativeValue() {
        StockItem item = StockItem.builder()
                .id(UUID.randomUUID())
                .purchasePrice(BigDecimal.valueOf(100))
                .unitSize(BigDecimal.ZERO) // guarded division-by-zero case (audit II.1)
                .build();

        item.computePricePerUse();

        assertThat(item.getPricePerUse()).isNotNull();
        assertThat(item.getPricePerUse().signum()).isGreaterThanOrEqualTo(0);
    }
}
