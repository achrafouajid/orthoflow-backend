package com.orthoflow.inventory.domain.model;

public enum StockCategory {
    ANESTHESIA,
    CONSUMABLES,
    ORTHODONTIC_PARTS,
    HYGIENE,
    INSTRUMENTS,
    MEDICATION;

    public static StockCategory fromString(String value) {
        if (value == null) return null;
        try {
            return StockCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CONSUMABLES;
        }
    }
}
