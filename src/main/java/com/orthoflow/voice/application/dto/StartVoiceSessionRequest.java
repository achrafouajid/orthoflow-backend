package com.orthoflow.voice.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StartVoiceSessionRequest {
    private UUID patientId;
    private String locale;
}
