package com.voxcare.voice.service;

import com.voxcare.voice.config.VoiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class SchedulingContextClient {

    private static final Logger log = LoggerFactory.getLogger(SchedulingContextClient.class);

    private final VoiceProperties properties;

    public SchedulingContextClient(VoiceProperties properties) {
        this.properties = properties;
    }

    public List<OpenSlot> findOpenSlots(Long providerId) {
        try {
            StringBuilder uri = new StringBuilder(properties.getProviderServiceBaseUrl())
                    .append("/providers/slots/open");
            if (providerId != null) {
                uri.append("?providerId=").append(providerId);
            }
            RestClient client = RestClient.builder().build();
            List<Map<String, Object>> body = client.get()
                    .uri(uri.toString())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            if (body == null) {
                return List.of();
            }
            List<OpenSlot> slots = new ArrayList<>();
            for (Map<String, Object> row : body) {
                slots.add(new OpenSlot(
                        asLong(row.get("providerId")),
                        stringVal(row.get("startTime")),
                        stringVal(row.get("endTime"))));
            }
            return slots;
        } catch (RestClientException e) {
            log.warn("Open slot lookup failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ReminderAppointment> findAppointmentsNeedingReminders() {
        try {
            RestClient client = RestClient.builder()
                    .baseUrl(properties.getAppointmentServiceBaseUrl())
                    .build();
            List<Map<String, Object>> body = client.get()
                    .uri("/appointments/reminders")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            if (body == null) {
                return List.of();
            }
            List<ReminderAppointment> appointments = new ArrayList<>();
            for (Map<String, Object> row : body) {
                appointments.add(new ReminderAppointment(
                        asLong(row.get("id")),
                        asLong(row.get("patientId")),
                        asLong(row.get("providerId")),
                        stringVal(row.get("appointmentDate") != null ? row.get("appointmentDate") : row.get("startTime")),
                        asInteger(row.get("durationMinutes"))));
            }
            return appointments;
        } catch (RestClientException e) {
            log.warn("Reminder appointment lookup failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private static Long asLong(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer asInteger(Object value) {
        if (value instanceof Number n) {
            return n.intValue();
        }
        return null;
    }

    private static String stringVal(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
