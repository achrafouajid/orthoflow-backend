package com.orthoflow.clinical.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateToothStateRequest {

    @NotBlank
    private String status;

    private String notes;

    /** Which UI surface made the change: '2d', '3d_top', '3d_frontal', '3d_internal', '3d_roots'. */
    @NotBlank
    private String source;
}
