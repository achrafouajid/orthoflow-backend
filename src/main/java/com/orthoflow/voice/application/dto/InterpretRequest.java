package com.orthoflow.voice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An utterance the on-device grammar could not resolve, plus just enough
 * context to resolve it.
 *
 * <p>Deliberately absent: the patient's name, id, or any part of their record.
 * Context arrives as shape rather than identity — <em>whether</em> a patient is
 * open, which tooth is selected, which module is active. The transcript itself
 * may of course contain a name the doctor spoke, which is precisely why this
 * whole path is off by default.
 */
@Getter
@Setter
public class InterpretRequest {

    @NotBlank
    private String transcript;

    /** e.g. fr-MA, fr-FR, en-US, ar-MA. */
    private String locale;

    /** Active module: 'patient-dossier', 'dental-chart', 'schedule', 'billing'… */
    private String module;

    /** FDI code of the tooth currently selected, if any — resolves "that tooth". */
    private String selectedFdi;

    private boolean patientContext;

    /** 'adult' or 'child' — a child chart has no tooth 16. */
    private String chartType;

    /**
     * The last few resolved utterances, oldest first. Carries the conversation
     * so "no, actually crown replacement" can attach to what preceded it.
     */
    private List<String> recentUtterances = new ArrayList<>();

    @NotNull
    private List<IntentDescriptor> availableIntents = new ArrayList<>();

    /** The clinical finding vocabulary the caller will accept. */
    private List<String> findingCodes = new ArrayList<>();

    /** Correlates the interpretation with the session it happened in. */
    private UUID sessionId;
}
