package com.voxcare.voice.controller;

import com.voxcare.voice.model.VoiceCall;
import com.voxcare.voice.orchestrator.CommunicationOrchestrator;
import com.voxcare.voice.provider.CommunicationStatus;
import com.voxcare.voice.repository.VoiceCallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

/**
 * EstateCraft webhook path: POST /webhooks/dial
 * Also mounted under /voice/webhooks/dial for gateway convenience.
 */
@RestController
public class DialWebhookController {

    private static final Logger log = LoggerFactory.getLogger(DialWebhookController.class);

    private final VoiceCallRepository voiceCallRepository;
    private final CommunicationOrchestrator orchestrator;

    public DialWebhookController(
            VoiceCallRepository voiceCallRepository,
            CommunicationOrchestrator orchestrator) {
        this.voiceCallRepository = voiceCallRepository;
        this.orchestrator = orchestrator;
    }

    @PostMapping({"/webhooks/dial", "/voice/webhooks/dial"})
    public ResponseEntity<Map<String, Object>> dialWebhook(@RequestBody Map<String, Object> body) {
        log.info("Dial webhook received type={}", body.getOrDefault("type", body.get("event")));

        Object externalIdObj = firstNonNull(body.get("call_id"), body.get("id"), nested(body, "data", "id"));
        if (externalIdObj == null) {
            return ResponseEntity.ok(Map.of("success", true));
        }
        String externalId = String.valueOf(externalIdObj);
        VoiceCall call = voiceCallRepository.findByExternalId(externalId).orElse(null);
        if (call == null) {
            log.warn("Dial webhook for unknown externalId={}", externalId);
            return ResponseEntity.ok(Map.of("success", true));
        }

        Object statusObj = firstNonNull(body.get("status"), nested(body, "data", "status"));
        String mapped = statusObj == null ? call.getStatus() : normalizeStatus(String.valueOf(statusObj));
        Object duration = firstNonNull(body.get("duration"), nested(body, "data", "duration"));
        Integer durationSeconds = duration instanceof Number n ? n.intValue() : null;
        Object transcript = firstNonNull(body.get("transcript"), nested(body, "data", "transcript"));

        orchestrator.handleProviderWebhook(
                call,
                mapped,
                durationSeconds,
                transcript == null ? null : String.valueOf(transcript));

        return ResponseEntity.ok(Map.of("success", true));
    }

    private static String normalizeStatus(String raw) {
        String s = raw.toLowerCase(Locale.ROOT).replace('-', '_');
        return switch (s) {
            case "in_progress" -> CommunicationStatus.IN_PROGRESS.name();
            case "no_answer" -> CommunicationStatus.NO_ANSWER.name();
            case "canceled" -> CommunicationStatus.CANCELLED.name();
            default -> s.toUpperCase(Locale.ROOT);
        };
    }

    private static Object firstNonNull(Object... values) {
        for (Object v : values) {
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    private static Object nested(Map<String, Object> map, String... path) {
        Object current = map;
        for (String key : path) {
            if (!(current instanceof Map<?, ?> m)) {
                return null;
            }
            current = m.get(key);
        }
        return current;
    }
}
