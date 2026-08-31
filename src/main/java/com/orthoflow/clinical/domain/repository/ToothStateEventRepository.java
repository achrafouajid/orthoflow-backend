package com.orthoflow.clinical.domain.repository;

import com.orthoflow.clinical.domain.model.ToothStateEvent;

import java.util.List;
import java.util.UUID;

public interface ToothStateEventRepository {
    ToothStateEvent save(ToothStateEvent event);
    List<ToothStateEvent> findByPatientId(UUID patientId);
}
