package com.voxcare.voice.orchestrator;

import com.voxcare.voice.service.PatientInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoiceRuleConfigTest {

    @Test
    void personalizesEstateCraftLeadNamePlaceholder() {
        VoiceRuleConfig rule = new VoiceRuleConfig(
                1L, "Booking", true, 0, 3, 30, true,
                "Hi {{leadName}}, call us back!",
                "Greet {{leadName}} and book a visit.",
                10);
        PatientInfo patient = new PatientInfo(1L, "John", "Doe", "+15551234567");
        assertEquals("Greet John Doe and book a visit.", rule.renderInstruction(patient));
        assertEquals("Hi John Doe, call us back!", rule.renderSms(patient));
    }
}
