package com.voxcare.voice.controller;

import com.voxcare.voice.service.VoiceCallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/voice/webhooks")
public class DialWebhookController {

    private static final Logger log = LoggerFactory.getLogger(DialWebhookController.class);

    private final VoiceCallService voiceCallService;

    public DialWebhookController(VoiceCallService voiceCallService) {
        this.voiceCallService = voiceCallService;
    }

    @PostMapping("/dial")
    public ResponseEntity<Map<String, Object>> dialWebhook(@RequestBody Map<String, Object> body) {
        log.info("Dial webhook received type={}", body.getOrDefault("type", body.get("event")));
        voiceCallService.handleDialWebhook(body);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
