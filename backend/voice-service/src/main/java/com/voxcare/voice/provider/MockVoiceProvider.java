package com.voxcare.voice.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local development provider — no external API calls.
 */
public class MockVoiceProvider implements VoiceProvider {

    private static final Logger log = LoggerFactory.getLogger(MockVoiceProvider.class);
    private final Map<String, CallStatusResult> calls = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "mock";
    }

    @Override
    public CallResult initiateCall(InitiateCallParams params) {
        String callId = "mock_" + UUID.randomUUID();
        log.info("Mock initiateCall id={} purpose={} patientId={}", callId, params.purpose(), params.patientId());
        CallStatusResult status = new CallStatusResult(
                callId, callId, CommunicationStatus.INITIATED, null, null, null);
        calls.put(callId, status);
        return new CallResult(callId, callId, CommunicationStatus.INITIATED, getName());
    }

    @Override
    public CallStatusResult getCallStatus(String callId) {
        return calls.getOrDefault(callId,
                new CallStatusResult(callId, callId, CommunicationStatus.COMPLETED, CallOutcome.CONNECTED, 45,
                        "Mock transcript: patient confirmed appointment."));
    }

    @Override
    public SmsResult sendSms(SendSmsParams params) {
        String messageId = "mock_sms_" + UUID.randomUUID();
        log.info("Mock sendSms id={} patientId={}", messageId, params.patientId());
        return new SmsResult(messageId, messageId, CommunicationStatus.DELIVERED, getName());
    }

    @Override
    public List<PhoneNumberInfo> listNumbers() {
        return List.of(new PhoneNumberInfo("mock-number-1", "+15550001111"));
    }
}
