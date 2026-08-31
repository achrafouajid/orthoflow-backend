package com.orthoflow.inventory.application.service;

import com.orthoflow.inventory.application.port.ConsumableLedger;
import com.orthoflow.inventory.domain.model.MovementType;
import com.orthoflow.inventory.domain.model.SourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * The only implementation of {@link ConsumableLedger} — every module outside
 * {@code inventory} depends on the port, never on this class or on
 * {@code StockService} directly.
 *
 * <p>Delegates straight to {@link StockService#recordMovement}, which already
 * holds the pessimistic write lock and the insufficient-stock guard (see its
 * own doc comment) — this class only translates the three business verbs into
 * the {@link MovementType} that method expects, so callers outside
 * {@code inventory} never need to know that enum exists.
 */
@Service
@RequiredArgsConstructor
public class ConsumableLedgerService implements ConsumableLedger {

    private final StockService stockService;

    @Override
    public void receive(UUID stockItemId, BigDecimal quantity, SourceType sourceType, UUID sourceId,
                         String sourceReference, String notes, UUID actorId) {
        stockService.recordMovement(stockItemId, MovementType.IN, quantity, sourceType, sourceId,
                sourceReference, notes, actorId);
    }

    @Override
    public void consume(UUID stockItemId, BigDecimal quantity, SourceType sourceType, UUID sourceId,
                         String sourceReference, String notes, UUID actorId) {
        stockService.recordMovement(stockItemId, MovementType.OUT, quantity, sourceType, sourceId,
                sourceReference, notes, actorId);
    }

    @Override
    public void restore(UUID stockItemId, BigDecimal quantity, SourceType sourceType, UUID sourceId,
                         String sourceReference, String notes, UUID actorId) {
        stockService.recordMovement(stockItemId, MovementType.RETURN, quantity, sourceType, sourceId,
                sourceReference, notes, actorId);
    }
}
