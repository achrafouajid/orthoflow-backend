package com.orthoflow.billing.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceLineRequest {

    @NotBlank
    private String actCode;

    @NotBlank
    private String label;

    @NotNull
    @DecimalMin(value = "0.0001", message = "quantity must be greater than zero")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "unitPrice cannot be negative")
    private BigDecimal unitPrice;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "discountPct cannot be negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "discountPct cannot exceed 100")
    private BigDecimal discountPct;

    private Integer sortOrder;
}
