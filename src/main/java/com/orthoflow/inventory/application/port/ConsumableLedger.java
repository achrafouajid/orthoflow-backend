package com.orthoflow.inventory.application.port;

import com.orthoflow.inventory.domain.model.SourceType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * The published port other modules use to move stock, instead of injecting
 * {@code inventory.application.service.StockService} directly and calling its
 * general-purpose {@code recordMovement}.
 *
 * <p>{@code procurement} and {@code treatment} still read {@code StockItem}
 * (and, for {@code procurement}, {@code Supplier}) as real JPA entities — that
 * decoupling is a separate, larger piece of work (dropping the FK constraints
 * and replacing them with a resolved-at-read-time summary, the way
 * {@code patient.application.port.PatientLookup} already works for
 * {@code Patient}) that this pass deliberately left for later. This port
 * narrows the *write* side only: every place outside {@code inventory} that
 * used to call {@code StockService.recordMovement} — {@code DeliveryNoteService}
 * receiving a delivery, {@code PatientTreatmentService} deducting and
 * restoring consumables, {@code TreatmentInvoiceService} finalising and
 * cancelling a session, {@code SalesOrderService} confirming and cancelling a
 * sale — now goes through here instead.
 *
 * <p>The three verbs are exactly the three {@code MovementType} values any
 * caller outside {@code inventory} ever used: receiving stock in, consuming it
 * out, and restoring what was consumed. {@code ADJUSTMENT} and
 * {@code WRITE_OFF} stay inventory-internal — a count-session correction or a
 * manual write-off is inventory's own business, not something procurement or
 * treatment initiates.
 */
public interface ConsumableLedger {

    /** Increases stock — e.g. a delivery note being received. */
    void receive(UUID stockItemId, BigDecimal quantity, SourceType sourceType, UUID sourceId,
                 String sourceReference, String notes, UUID actorId);

    /** Decreases stock — e.g. a treatment session consuming its recipe. */
    void consume(UUID stockItemId, BigDecimal quantity, SourceType sourceType, UUID sourceId,
                 String sourceReference, String notes, UUID actorId);

    /** Reverses an earlier {@link #consume} — e.g. a completed treatment reopened or cancelled. */
    void restore(UUID stockItemId, BigDecimal quantity, SourceType sourceType, UUID sourceId,
                 String sourceReference, String notes, UUID actorId);
}
