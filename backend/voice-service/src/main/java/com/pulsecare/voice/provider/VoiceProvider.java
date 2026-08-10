package com.pulsecare.voice.provider;

import java.util.List;

/**
 * Vendor-neutral voice/SMS contract (mirrors EstateCraft IVoiceProvider).
 * Dial-specific details must not leak outside DialVoiceProvider.
 */
public interface VoiceProvider {

    String getName();

    CallResult initiateCall(InitiateCallParams params);

    CallStatusResult getCallStatus(String callId);

    SmsResult sendSms(SendSmsParams params);

    List<PhoneNumberInfo> listNumbers();
}
