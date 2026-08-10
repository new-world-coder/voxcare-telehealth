package com.pulsecare.voice.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * GetDial REST adapter — ported from EstateCraft DialVoiceProvider.
 * API: https://api.getdial.ai (Bearer auth, camelCase payloads).
 */
public class DialVoiceProvider implements VoiceProvider {

    private static final Logger log = LoggerFactory.getLogger(DialVoiceProvider.class);

    private final RestClient client;
    private final String defaultFromNumberId;

    public DialVoiceProvider(String apiKey, String baseUrl, String fromNumberId) {
        this.defaultFromNumberId = fromNumberId;
        String root = (baseUrl == null || baseUrl.isBlank()) ? "https://api.getdial.ai" : baseUrl;
        this.client = RestClient.builder()
                .baseUrl(root)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public String getName() {
        return "dial";
    }

    @Override
    public CallResult initiateCall(InitiateCallParams params) {
        String fromNumberId = resolveFromNumberId(params.fromNumberId());
        Map<String, Object> payload = new HashMap<>();
        payload.put("to", params.to());
        payload.put("fromNumberId", fromNumberId);
        payload.put("outboundInstruction", params.outboundInstruction());

        log.info("Dial initiateCall to={} fromNumberId={} patientId={}",
                maskPhone(params.to()), fromNumberId, params.patientId());

        Map<?, ?> data = client.post()
                .uri("/v1/calls")
                .body(payload)
                .retrieve()
                .body(Map.class);

        Map<?, ?> call = extractEntity(data, "call");
        String callId = stringVal(call.get("id"));
        if (callId == null || callId.isBlank()) {
            throw new IllegalStateException("Dial API returned unexpected call payload: " + data);
        }

        CommunicationStatus status = mapStatus(extractStatus(call.get("status")));
        return new CallResult(callId, callId, status, getName());
    }

    @Override
    public CallStatusResult getCallStatus(String callId) {
        Map<?, ?> data = client.get()
                .uri("/v1/calls/{id}", callId)
                .retrieve()
                .body(Map.class);
        Map<?, ?> call = extractEntity(data, "call");
        CommunicationStatus status = mapStatus(extractStatus(call.get("status")));
        CallOutcome outcome = null;
        if (status == CommunicationStatus.COMPLETED) {
            outcome = CallOutcome.CONNECTED;
        } else if (status == CommunicationStatus.NO_ANSWER) {
            outcome = CallOutcome.NO_ANSWER;
        } else if (status == CommunicationStatus.BUSY) {
            outcome = CallOutcome.BUSY;
        } else if (status == CommunicationStatus.FAILED) {
            outcome = CallOutcome.FAILED;
        }
        Integer duration = call.get("duration") instanceof Number n ? n.intValue() : null;
        String transcript = call.get("transcript") != null ? String.valueOf(call.get("transcript")) : null;
        return new CallStatusResult(callId, stringVal(call.get("id")), status, outcome, duration, transcript);
    }

    @Override
    public SmsResult sendSms(SendSmsParams params) {
        String fromNumberId = resolveFromNumberId(params.fromNumberId());
        Map<String, Object> payload = new HashMap<>();
        payload.put("to", params.to());
        payload.put("fromNumberId", fromNumberId);
        payload.put("body", params.body());

        Map<?, ?> data = client.post()
                .uri("/v1/messages")
                .body(payload)
                .retrieve()
                .body(Map.class);

        Map<?, ?> message = extractEntity(data, "message");
        String messageId = stringVal(message.get("id"));
        if (messageId == null || messageId.isBlank()) {
            throw new IllegalStateException("Dial API returned unexpected message payload: " + data);
        }
        CommunicationStatus status = mapStatus(extractStatus(message.get("status")));
        return new SmsResult(messageId, messageId, status, getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PhoneNumberInfo> listNumbers() {
        Object data = client.get()
                .uri("/v1/numbers")
                .retrieve()
                .body(Object.class);

        List<Map<String, Object>> numbers = new ArrayList<>();
        if (data instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> m) {
                    numbers.add((Map<String, Object>) m);
                }
            }
        } else if (data instanceof Map<?, ?> map && map.get("numbers") instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> m) {
                    numbers.add((Map<String, Object>) m);
                }
            }
        }

        return numbers.stream()
                .map(n -> new PhoneNumberInfo(String.valueOf(n.get("id")), String.valueOf(n.get("number"))))
                .toList();
    }

    private String resolveFromNumberId(String explicit) {
        if (explicit != null && !explicit.isBlank()) {
            return explicit;
        }
        if (defaultFromNumberId != null && !defaultFromNumberId.isBlank()) {
            return defaultFromNumberId;
        }
        List<PhoneNumberInfo> numbers = listNumbers();
        if (numbers.isEmpty()) {
            throw new IllegalStateException(
                    "No Dial phone numbers available. Provision a number or set DIAL_FROM_NUMBER_ID.");
        }
        return numbers.get(0).id();
    }

    private static Map<?, ?> extractEntity(Map<?, ?> data, String key) {
        if (data == null) {
            return Map.of();
        }
        if (data.containsKey(key) && data.get(key) instanceof Map<?, ?> nested) {
            return nested;
        }
        return data;
    }

    private static String extractStatus(Object status) {
        if (status == null) {
            return "pending";
        }
        if (status instanceof String s) {
            return s;
        }
        if (status instanceof Map<?, ?> map) {
            Object state = map.get("state");
            if (state != null) {
                return String.valueOf(state);
            }
            Object label = map.get("label");
            if (label != null) {
                return String.valueOf(label);
            }
        }
        return "pending";
    }

    private static CommunicationStatus mapStatus(String dialStatus) {
        return switch (dialStatus.toLowerCase(Locale.ROOT)) {
            case "queued" -> CommunicationStatus.QUEUED;
            case "initiated" -> CommunicationStatus.INITIATED;
            case "ringing" -> CommunicationStatus.RINGING;
            case "in_progress", "in-progress" -> CommunicationStatus.IN_PROGRESS;
            case "completed" -> CommunicationStatus.COMPLETED;
            case "delivered" -> CommunicationStatus.DELIVERED;
            case "failed" -> CommunicationStatus.FAILED;
            case "no_answer", "no-answer" -> CommunicationStatus.NO_ANSWER;
            case "busy" -> CommunicationStatus.BUSY;
            case "cancelled", "canceled" -> CommunicationStatus.CANCELLED;
            default -> CommunicationStatus.PENDING;
        };
    }

    private static String stringVal(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "****" + phone.substring(phone.length() - 4);
    }
}
