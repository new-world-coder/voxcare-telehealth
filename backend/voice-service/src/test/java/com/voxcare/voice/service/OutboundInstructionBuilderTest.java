package com.voxcare.voice.service;

import com.voxcare.voice.config.VoiceProperties;
import com.voxcare.voice.model.VoiceCallPurpose;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutboundInstructionBuilderTest {

    @Test
    void bookingInstructionIncludesSlotsAndPatientName() {
        VoiceProperties props = new VoiceProperties();
        props.setDefaultClinicName("VoxCare");
        OutboundInstructionBuilder builder = new OutboundInstructionBuilder(props);

        PatientInfo patient = new PatientInfo(1L, "John", "Doe", "+15550123");
        List<OpenSlot> slots = List.of(new OpenSlot(9L, "2026-08-17T09:00:00", "2026-08-17T10:00:00"));

        String instruction = builder.build(VoiceCallPurpose.BOOKING, patient, slots, null);

        assertTrue(instruction.contains("John Doe"));
        assertTrue(instruction.contains("VoxCare"));
        assertTrue(instruction.contains("2026-08-17T09:00:00"));
        assertTrue(instruction.contains("Provider 9"));
    }

    @Test
    void smsFallbackMentionsClinic() {
        VoiceProperties props = new VoiceProperties();
        props.setDefaultClinicName("VoxCare");
        OutboundInstructionBuilder builder = new OutboundInstructionBuilder(props);
        String sms = builder.smsFallbackBody(VoiceCallPurpose.REMINDER, null);
        assertTrue(sms.contains("VoxCare"));
        assertTrue(sms.toLowerCase().contains("reminder"));
    }
}
