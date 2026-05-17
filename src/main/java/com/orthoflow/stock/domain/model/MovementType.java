package com.orthoflow.stock.domain.model;

public enum MovementType {
    IN,         // Stock added (delivery received, or manual increase)
    OUT,        // Stock deducted (treatment invoiced, sales order)
    ADJUSTMENT, // Manual correction
    RETURN,     // Returned to supplier or compensating movement on cancel
    WRITE_OFF   // Expired or damaged
}
