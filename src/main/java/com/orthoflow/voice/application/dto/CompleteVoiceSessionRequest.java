package com.orthoflow.voice.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteVoiceSessionRequest {
    /** COMPLETED | ABANDONED */
    private String status = "COMPLETED";

    /**
     * The consultation summary as the doctor reviewed and confirmed it,
     * serialised as JSON. Frozen here so a later edit to an underlying finding
     * cannot silently rewrite what was signed off.
     */
    private String summary;

    /** True once the doctor has explicitly confirmed the summary. */
    private boolean confirmed;
}
