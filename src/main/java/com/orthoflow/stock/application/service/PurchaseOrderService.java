package com.orthoflow.stock.application.service;

import com.orthoflow.stock.domain.model.POStatus;
import com.orthoflow.stock.domain.model.PurchaseOrder;
import com.orthoflow.stock.domain.model.PurchaseOrderLine;
import com.orthoflow.stock.domain.model.Supplier;
import com.orthoflow.stock.domain.repository.PurchaseOrderRepository;
import com.orthoflow.stock.domain.repository.StockItemRepository;
import com.orthoflow.stock.domain.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final StockItemRepository stockItemRepository;

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public Optional<PurchaseOrder> getPurchaseOrderById(UUID id) {
        return purchaseOrderRepository.findById(id);
    }

    public Optional<PurchaseOrder> getPurchaseOrderByNumber(String poNumber) {
        return purchaseOrderRepository.findByPoNumber(poNumber);
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder order) {
        if (order.getSupplier() == null || order.getSupplier().getId() == null) {
            throw new IllegalArgumentException("Supplier ID is required");
        }
        Supplier supplier = supplierRepository.findById(order.getSupplier().getId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + order.getSupplier().getId()));
        order.setSupplier(supplier);

        // Generate PO number if not present
        if (order.getPoNumber() == null || order.getPoNumber().isEmpty()) {
            order.setPoNumber(generatePoNumber());
        }

        order.setStatus(POStatus.DRAFT);
        order.setOrderDate(LocalDate.now());

        if (order.getLines() != null) {
            for (PurchaseOrderLine line : order.getLines()) {
                line.setPurchaseOrder(order);
                if (line.getStockItem() == null || line.getStockItem().getId() == null) {
                    throw new IllegalArgumentException("Stock Item ID is required for order lines");
                }
                line.setStockItem(stockItemRepository.findById(line.getStockItem().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + line.getStockItem().getId())));
                line.calculateTotalPrice();
            }
        }

        order.calculateTotal();
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder updatePurchaseOrderStatus(UUID id, POStatus status) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found: " + id));
        order.setStatus(status);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public void deletePurchaseOrder(UUID id) {
        purchaseOrderRepository.deleteById(id);
    }

    private synchronized String generatePoNumber() {
        int nextVal = purchaseOrderRepository.findAll().size() + 1;
        return "PO-" + LocalDate.now().getYear() + "-" + String.format("%04d", nextVal);
    }
}
