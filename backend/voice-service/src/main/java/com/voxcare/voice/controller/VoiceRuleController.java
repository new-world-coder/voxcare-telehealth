package com.voxcare.voice.controller;

import com.voxcare.voice.dto.VoiceRuleRequest;
import com.voxcare.voice.dto.VoiceRuleResponse;
import com.voxcare.voice.model.VoiceRule;
import com.voxcare.voice.repository.VoiceRuleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * EstateCraft-compatible voice rules API.
 */
@RestController
@RequestMapping("/voice-rules")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class VoiceRuleController {

    private final VoiceRuleRepository repository;

    public VoiceRuleController(VoiceRuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<VoiceRuleResponse> list() {
        return repository.findAll().stream().map(VoiceRuleResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<VoiceRuleResponse> create(@Valid @RequestBody VoiceRuleRequest request) {
        VoiceRule rule = new VoiceRule();
        apply(rule, request);
        return new ResponseEntity<>(VoiceRuleResponse.from(repository.save(rule)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public VoiceRuleResponse update(@PathVariable Long id, @Valid @RequestBody VoiceRuleRequest request) {
        VoiceRule rule = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voice rule not found"));
        apply(rule, request);
        return VoiceRuleResponse.from(repository.save(rule));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Voice rule not found");
        }
        repository.deleteById(id);
    }

    private void apply(VoiceRule rule, VoiceRuleRequest request) {
        rule.setName(request.getName());
        rule.setEnabled(request.getEnabled() == null || request.getEnabled());
        rule.setMinQualificationScore(request.getMinQualificationScore() == null ? 70 : request.getMinQualificationScore());
        rule.setMaxRetries(request.getMaxRetries() == null ? 3 : request.getMaxRetries());
        rule.setRetryDelayMinutes(request.getRetryDelayMinutes() == null ? 30 : request.getRetryDelayMinutes());
        rule.setSmsFallbackEnabled(request.getSmsFallbackEnabled() == null || request.getSmsFallbackEnabled());
        rule.setSmsFallbackTemplate(request.getSmsFallbackTemplate());
        rule.setOutboundInstruction(request.getOutboundInstruction());
        rule.setPriority(request.getPriority() == null ? 0 : request.getPriority());
    }
}
