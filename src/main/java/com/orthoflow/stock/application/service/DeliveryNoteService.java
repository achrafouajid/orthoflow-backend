package com.orthoflow.stock.application.service;

import com.orthoflow.stock.domain.model.*;
import com.orthoflow.stock.domain.repository.DeliveryNoteRepository;
import com.orthoflow.stock.domain.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryNoteService {

    private final DeliveryNoteRepository deliveryNoteRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final StockService stockService;

    public List<DeliveryNote> getAllDeliveryNotes() {
        return deliveryNoteRepository.findAll();
    }

    public Optional<DeliveryNote> getDeliveryNoteById(UUID id) {
        return deliveryNoteRepository.findById(id);
    }

    public Optional<DeliveryNote> getDeliveryNoteByNumber(String dnNumber) {
        return deliveryNoteRepository.findByDnNumber(dnNumber);
    }

    @Transactional
    public DeliveryNote createDeliveryNote(DeliveryNote note) {
        if (note.getPurchaseOrder() == null || note.getPurchaseOrder().getId() == null) {
            throw new IllegalArgumentException("Purchase Order is required");
        }
        PurchaseOrder po = purchaseOrderRepository.findById(note.getPurchaseOrder().getId())
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found: " + note.getPurchaseOrder().getId()));
        note.setPurchaseOrder(po);
        note.setSupplier(po.getSupplier());

        if (note.getDnNumber() == null || note.getDnNumber().isEmpty()) {
            note.setDnNumber(generateDnNumber());
        }

        note.setStatus(DNStatus.PENDING);

        if (note.getLines() != null) {
            for (DeliveryNoteLine line : note.getLines()) {
                line.setDeliveryNote(note);
                // Find matching PO line
                if (line.getPoLine() == null || line.getPoLine().getId() == null) {
                    throw new IllegalArgumentException("PO Line ID is required for delivery lines");
                }
                PurchaseOrderLine poLine = po.getLines().stream()
                        .filter(l -> l.getId().equals(line.getPoLine().getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("PO Line not found on parent order"));
                line.setPoLine(poLine);
                line.setStockItem(poLine.getStockItem());
            }
        }

        return deliveryNoteRepository.save(note);
    }

    @Transactional
    public DeliveryNote receiveDeliveryNote(UUID id, UUID receivedBy, String notes) {
        DeliveryNote note = deliveryNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery note not found: " + id));

        if (note.getStatus() == DNStatus.RECEIVED) {
            throw new IllegalStateException("Delivery note has already been received.");
        }

        note.setStatus(DNStatus.RECEIVED);
        note.setReceivedDate(LocalDate.now());
        note.setReceivedBy(receivedBy);
        if (notes != null && !notes.isEmpty()) {
            note.setNotes(notes);
        }

        // Generate stock movements for each line
        if (!note.isStockMovementsGenerated()) {
            PurchaseOrder po = note.getPurchaseOrder();
            for (DeliveryNoteLine line : note.getLines()) {
                BigDecimal qtyReceived = line.getQuantityReceived();
                if (qtyReceived != null && qtyReceived.compareTo(BigDecimal.ZERO) > 0) {
                    // Update Stock Item and record movement
                    stockService.recordMovement(
                            line.getStockItem().getId(),
                            MovementType.IN,
                            qtyReceived,
                            SourceType.DELIVERY_NOTE,
                            note.getId(),
                            note.getDnNumber(),
                            "Received from Delivery Note " + note.getDnNumber(),
                            receivedBy
                    );

                    // Update quantity received on PO line
                    PurchaseOrderLine poLine = line.getPoLine();
                    BigDecimal currentPoReceived = poLine.getQuantityReceived() != null ? poLine.getQuantityReceived() : BigDecimal.ZERO;
                    poLine.setQuantityReceived(currentPoReceived.add(qtyReceived));
                }
            }

            note.setStockMovementsGenerated(true);

            // Re-evaluate Purchase Order Status
            boolean allFullyReceived = true;
            boolean anyReceived = false;

            for (PurchaseOrderLine poLine : po.getLines()) {
                BigDecimal ordered = poLine.getQuantityOrdered();
                BigDecimal received = poLine.getQuantityReceived() != null ? poLine.getQuantityReceived() : BigDecimal.ZERO;

                if (received.compareTo(BigDecimal.ZERO) > 0) {
                    anyReceived = true;
                }
                if (received.compareTo(ordered) < 0) {
                    allFullyReceived = false;
                }
            }

            if (allFullyReceived) {
                po.setStatus(POStatus.RECEIVED);
            } else if (anyReceived) {
                po.setStatus(POStatus.PARTIAL);
            }
            purchaseOrderRepository.save(po);
        }

        return deliveryNoteRepository.save(note);
    }

    @Transactional
    public void deleteDeliveryNote(UUID id) {
        deliveryNoteRepository.deleteById(id);
    }

    private synchronized String generateDnNumber() {
        int nextVal = deliveryNoteRepository.findAll().size() + 1;
        return "DN-" + LocalDate.now().getYear() + "-" + String.format("%04d", nextVal);
    }
}
