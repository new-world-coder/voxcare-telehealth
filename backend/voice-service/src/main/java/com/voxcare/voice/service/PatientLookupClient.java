package com.voxcare.voice.service;

import com.voxcare.voice.config.VoiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class PatientLookupClient {

    private static final Logger log = LoggerFactory.getLogger(PatientLookupClient.class);

    private final VoiceProperties properties;

    public PatientLookupClient(VoiceProperties properties) {
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public String findPhoneByPatientId(Long patientId) {
        if (patientId == null) {
            return null;
        }
        try {
            RestClient client = RestClient.builder()
                    .baseUrl(properties.getPatientServiceBaseUrl())
                    .build();
            Map<?, ?> body = client.get()
                    .uri("/patients/{id}", patientId)
                    .retrieve()
                    .body(Map.class);
            if (body == null || body.get("phone") == null) {
                return null;
            }
            return String.valueOf(body.get("phone"));
        } catch (RestClientException e) {
            log.warn("Patient lookup failed for id={}: {}", patientId, e.getMessage());
            return null;
        }
    }
}
