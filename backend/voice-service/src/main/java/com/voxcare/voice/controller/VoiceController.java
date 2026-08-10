package com.voxcare.voice.controller;

import com.voxcare.voice.dto.InitiateVoiceCallRequest;
import com.voxcare.voice.dto.VoiceCallResponse;
import com.voxcare.voice.service.VoiceCallService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/voice")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class VoiceController {

    private final VoiceCallService voiceCallService;

    public VoiceController(VoiceCallService voiceCallService) {
        this.voiceCallService = voiceCallService;
    }

    @PostMapping("/calls")
    public ResponseEntity<VoiceCallResponse> initiateCall(@Valid @RequestBody InitiateVoiceCallRequest request) {
        return new ResponseEntity<>(voiceCallService.initiateCall(request), HttpStatus.CREATED);
    }

    @GetMapping("/calls/{id}")
    public VoiceCallResponse getById(@PathVariable Long id) {
        return voiceCallService.getById(id);
    }

    @GetMapping("/calls/external/{externalId}")
    public VoiceCallResponse getByExternalId(@PathVariable String externalId) {
        return voiceCallService.getByExternalId(externalId);
    }

    @GetMapping("/calls/patient/{patientId}")
    public List<VoiceCallResponse> listByPatient(@PathVariable Long patientId) {
        return voiceCallService.listByPatient(patientId);
    }

    @PostMapping("/calls/{id}/retry")
    public VoiceCallResponse retryCall(@PathVariable Long id) {
        return voiceCallService.retryCall(id);
    }

    @GetMapping("/providers/health")
    public Map<String, Object> providerHealth() {
        return voiceCallService.providerHealth();
    }

    /**
     * Enqueue REMINDER calls for appointments returned by appointment-service.
     */
    @PostMapping("/reminders/enqueue")
    public Map<String, Object> enqueueReminders() {
        return voiceCallService.enqueueReminderCalls();
    }
}
