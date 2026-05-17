package com.orthoflow.stock.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentConsumableId implements Serializable {

    @Column(name = "treatment_id")
    private UUID treatmentId;

    @Column(name = "stock_item_id")
    private UUID stockItemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreatmentConsumableId that = (TreatmentConsumableId) o;
        return Objects.equals(treatmentId, that.treatmentId) &&
               Objects.equals(stockItemId, that.stockItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treatmentId, stockItemId);
    }
}
