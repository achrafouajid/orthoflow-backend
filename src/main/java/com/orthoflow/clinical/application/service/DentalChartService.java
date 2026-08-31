package com.orthoflow.clinical.application.service;

import com.orthoflow.patient.domain.repository.PatientRepository;
import com.orthoflow.clinical.application.dto.DentalChartResponse;
import com.orthoflow.clinical.application.dto.ToothStateResponse;
import com.orthoflow.clinical.application.dto.UpdateToothStateRequest;
import com.orthoflow.clinical.domain.model.ChartType;
import com.orthoflow.clinical.domain.model.DentalChart;
import com.orthoflow.clinical.domain.model.ToothState;
import com.orthoflow.clinical.domain.model.ToothStateEvent;
import com.orthoflow.clinical.domain.repository.DentalChartRepository;
import com.orthoflow.clinical.domain.repository.ToothStateEventRepository;
import com.orthoflow.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DentalChartService {

    private final DentalChartRepository dentalChartRepository;
    private final ToothStateEventRepository toothStateEventRepository;
    private final PatientRepository patientRepository;

    /**
     * Returns the patient's chart, creating an empty one (default chart type
     * "adult" unless overridden) the first time it is requested — the chart
     * that used to exist only in that browser's localStorage.
     */
    @Transactional
    public DentalChartResponse getOrCreateChart(UUID patientId, String requestedChartType) {
        if (!patientRepository.findById(patientId).isPresent()) {
            throw new NotFoundException("Patient not found: " + patientId);
        }

        DentalChart chart = dentalChartRepository.findByPatientId(patientId)
                .orElseGet(() -> dentalChartRepository.save(
                        DentalChart.builder()
                                .patientId(patientId)
                                .chartType(parseChartType(requestedChartType))
                                .build()
                ));

        return toResponse(chart);
    }

    @Transactional
    public DentalChartResponse updateTooth(UUID patientId, String fdi, UpdateToothStateRequest request, UUID actorId) {
        DentalChart chart = dentalChartRepository.findByPatientId(patientId)
                .orElseGet(() -> dentalChartRepository.save(
                        DentalChart.builder().patientId(patientId).chartType(ChartType.adult).build()
                ));

        ToothState existing = chart.getToothStates().stream()
                .filter(t -> t.getFdi().equals(fdi))
                .findFirst()
                .orElse(null);

        String previousStatus = existing != null ? existing.getStatus() : null;

        if (existing != null) {
            existing.setStatus(request.getStatus());
            existing.setNotes(request.getNotes());
        } else {
            ToothState toothState = ToothState.builder()
                    .chart(chart)
                    .fdi(fdi)
                    .status(request.getStatus())
                    .notes(request.getNotes())
                    .build();
            chart.getToothStates().add(toothState);
        }

        DentalChart saved = dentalChartRepository.save(chart);

        toothStateEventRepository.save(ToothStateEvent.builder()
                .patientId(patientId)
                .fdi(fdi)
                .previousStatus(previousStatus)
                .newStatus(request.getStatus())
                .notes(request.getNotes())
                .actorId(actorId)
                .source(request.getSource())
                .build());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ToothStateEvent> getEvents(UUID patientId) {
        return toothStateEventRepository.findByPatientId(patientId);
    }

    private ChartType parseChartType(String value) {
        if (value == null) return ChartType.adult;
        try {
            return ChartType.valueOf(value.toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return ChartType.adult;
        }
    }

    private DentalChartResponse toResponse(DentalChart chart) {
        List<ToothStateResponse> teeth = chart.getToothStates().stream()
                .map(t -> ToothStateResponse.builder().fdi(t.getFdi()).status(t.getStatus()).notes(t.getNotes()).build())
                .collect(Collectors.toList());

        return DentalChartResponse.builder()
                .id(chart.getId())
                .patientId(chart.getPatientId())
                .chartType(chart.getChartType().name())
                .teeth(teeth)
                .updatedAt(chart.getUpdatedAt())
                .build();
    }
}
