package com.orthoflow.stock.application.service;

import com.orthoflow.billing.domain.model.Patient;
import com.orthoflow.billing.domain.repository.PatientRepository;
import com.orthoflow.stock.domain.model.*;
import com.orthoflow.stock.domain.repository.SalesOrderRepository;
import com.orthoflow.stock.domain.repository.StockItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final PatientRepository patientRepository;
    private final StockItemRepository stockItemRepository;
    private final StockService stockService;

    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderRepository.findAll();
    }

    public Optional<SalesOrder> getSalesOrderById(UUID id) {
        return salesOrderRepository.findById(id);
    }

    public Optional<SalesOrder> getSalesOrderByNumber(String soNumber) {
        return salesOrderRepository.findBySoNumber(soNumber);
    }

    @Transactional
    public SalesOrder createSalesOrder(SalesOrder order) {
        if (order.getPatient() == null || order.getPatient().getId() == null) {
            throw new IllegalArgumentException("Patient is required");
        }
        Patient patient = patientRepository.findById(order.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + order.getPatient().getId()));
        order.setPatient(patient);

        if (order.getSoNumber() == null || order.getSoNumber().isEmpty()) {
            order.setSoNumber(generateSoNumber());
        }

        order.setStatus(SOStatus.DRAFT);

        if (order.getLines() != null) {
            for (SalesOrderLine line : order.getLines()) {
                line.setSalesOrder(order);
                if (line.getStockItem() == null || line.getStockItem().getId() == null) {
                    throw new IllegalArgumentException("Stock Item ID is required for lines");
                }
                line.setStockItem(stockItemRepository.findById(line.getStockItem().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + line.getStockItem().getId())));
                if (line.getUnitPrice() == null) {
                    line.setUnitPrice(line.getStockItem().getPurchasePrice()); // Or target retail price, let's use purchasePrice as fallback
                }
            }
        }

        order.calculateTotal();
        return salesOrderRepository.save(order);
    }

    @Transactional
    public SalesOrder confirmSalesOrder(UUID id, UUID confirmedBy) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales order not found: " + id));

        if (order.getStatus() != SOStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT sales orders can be confirmed.");
        }

        order.setStatus(SOStatus.CONFIRMED);

        // Deduct stock immediately
        if (order.getLines() != null) {
            for (SalesOrderLine line : order.getLines()) {
                stockService.recordMovement(
                        line.getStockItem().getId(),
                        MovementType.OUT,
                        line.getQuantity(),
                        SourceType.SALES_ORDER,
                        order.getId(),
                        order.getSoNumber(),
                        "Patient direct purchase: " + order.getSoNumber(),
                        confirmedBy
                );
            }
        }

        return salesOrderRepository.save(order);
    }

    @Transactional
    public SalesOrder cancelSalesOrder(UUID id, UUID cancelledBy) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales order not found: " + id));

        if (order.getStatus() == SOStatus.CANCELLED) {
            throw new IllegalStateException("Sales order is already cancelled.");
        }

        // Restore stock if it was previously confirmed
        if (order.getStatus() == SOStatus.CONFIRMED && order.getLines() != null) {
            for (SalesOrderLine line : order.getLines()) {
                stockService.recordMovement(
                        line.getStockItem().getId(),
                        MovementType.RETURN,
                        line.getQuantity(),
                        SourceType.SALES_ORDER,
                        order.getId(),
                        order.getSoNumber(),
                        "Restored due to Sales Order cancellation: " + order.getSoNumber(),
                        cancelledBy
                );
            }
        }

        order.setStatus(SOStatus.CANCELLED);
        return salesOrderRepository.save(order);
    }

    @Transactional
    public void deleteSalesOrder(UUID id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales order not found: " + id));
        if (order.getStatus() == SOStatus.CONFIRMED) {
            throw new IllegalStateException("Confirmed sales orders cannot be deleted.");
        }
        salesOrderRepository.deleteById(id);
    }

    private synchronized String generateSoNumber() {
        int nextVal = salesOrderRepository.findAll().size() + 1;
        return "SO-" + LocalDate.now().getYear() + "-" + String.format("%04d", nextVal);
    }
}
