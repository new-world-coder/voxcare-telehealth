package com.voxcare.voice.controller;

import com.voxcare.voice.dto.VoiceCallResponse;
import com.voxcare.voice.model.VoiceCallPurpose;
import com.voxcare.voice.orchestrator.CommunicationOrchestrator;
import com.voxcare.voice.orchestrator.VoiceRuleConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * EstateCraft-compatible communications API:
 * POST /communications/call  { patientId }  (EstateCraft used leadId)
 * GET  /communications/call/{id}/status
 */
@RestController
@RequestMapping("/communications")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class CommunicationsController {

    private final CommunicationOrchestrator orchestrator;

    public CommunicationsController(CommunicationOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/call")
    public ResponseEntity<Map<String, Object>> initiateCall(@RequestBody Map<String, Object> body) {
        Long patientId = asLong(body.get("patientId"));
        if (patientId == null) {
            // Accept EstateCraft field name for drop-in familiarity
            patientId = asLong(body.get("leadId"));
        }
        if (patientId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Bad Request",
                    "message", "patientId is required (EstateCraft alias: leadId)"));
        }

        VoiceRuleConfig rule = orchestrator.resolveActiveRule();
        VoiceCallPurpose purpose = parsePurpose(body.get("purpose"));
        Long providerId = asLong(body.get("providerId"));
        Long appointmentId = asLong(body.get("appointmentId"));
        String to = body.get("to") == null ? null : String.valueOf(body.get("to"));

        VoiceCallResponse call = orchestrator.triggerOutboundCall(
                patientId, to, providerId, appointmentId, purpose, rule, 0, null);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("data", call);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/call/{communicationId}/status")
    public Map<String, Object> pollStatus(@PathVariable Long communicationId) {
        VoiceCallResponse call = orchestrator.pollCallStatus(communicationId);
        return Map.of("success", true, "data", call);
    }

    private static VoiceCallPurpose parsePurpose(Object raw) {
        if (raw == null) {
            return VoiceCallPurpose.BOOKING;
        }
        try {
            return VoiceCallPurpose.valueOf(String.valueOf(raw).trim().toUpperCase());
        } catch (Exception e) {
            return VoiceCallPurpose.BOOKING;
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
}
