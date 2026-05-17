package com.orthoflow.stock.application.service;

import com.orthoflow.stock.domain.model.Treatment;
import com.orthoflow.stock.domain.model.TreatmentConsumable;
import com.orthoflow.stock.domain.repository.StockItemRepository;
import com.orthoflow.stock.domain.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final StockItemRepository stockItemRepository;

    public List<Treatment> getAllTreatments() {
        return treatmentRepository.findAll();
    }

    public Optional<Treatment> getTreatmentById(UUID id) {
        return treatmentRepository.findById(id);
    }

    public Optional<Treatment> getTreatmentByCode(String code) {
        return treatmentRepository.findByCode(code);
    }

    @Transactional
    public Treatment saveTreatment(Treatment treatment) {
        if (treatment.getConsumables() != null) {
            for (TreatmentConsumable consumable : treatment.getConsumables()) {
                consumable.setTreatment(treatment);
                if (consumable.getStockItem() != null && consumable.getStockItem().getId() != null) {
                    consumable.setStockItem(stockItemRepository.findById(consumable.getStockItem().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + consumable.getStockItem().getId())));
                }
            }
        }
        return treatmentRepository.save(treatment);
    }

    @Transactional
    public void deleteTreatment(UUID id) {
        treatmentRepository.deleteById(id);
    }
}
