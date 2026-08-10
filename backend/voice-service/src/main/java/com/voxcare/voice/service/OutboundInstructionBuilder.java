package com.voxcare.voice.service;

import com.voxcare.voice.config.VoiceProperties;
import com.voxcare.voice.model.VoiceCallPurpose;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OutboundInstructionBuilder {

    private final VoiceProperties properties;

    public OutboundInstructionBuilder(VoiceProperties properties) {
        this.properties = properties;
    }

    public String build(
            VoiceCallPurpose purpose,
            PatientInfo patient,
            List<OpenSlot> slots,
            String appointmentWhen) {
        String clinic = properties.getDefaultClinicName();
        String patientName = patient == null ? "the patient" : patient.displayName();
        String slotBlock = formatSlots(slots);

        return switch (purpose) {
            case BOOKING -> """
                    You are %s's appointment scheduling assistant speaking with %s.
                    Offer these available telehealth slots and book the one they confirm:
                    %s
                    Be concise and HIPAA-aware: do not discuss diagnoses. Confirm date and time clearly.
                    """.formatted(clinic, patientName, slotBlock).trim();
            case REMINDER -> """
                    You are %s's reminder assistant speaking with %s.
                    Remind them of their upcoming telehealth appointment%s.
                    Offer to reschedule if needed. Do not discuss clinical details.
                    Available alternate slots if they want to move it:
                    %s
                    """.formatted(
                    clinic,
                    patientName,
                    appointmentWhen == null || appointmentWhen.isBlank() ? "" : " on " + appointmentWhen,
                    slotBlock).trim();
            case RESCHEDULE -> """
                    You are %s's scheduling assistant speaking with %s.
                    Help them reschedule their telehealth appointment using these open slots:
                    %s
                    """.formatted(clinic, patientName, slotBlock).trim();
            case FOLLOW_UP -> """
                    You are %s's follow-up assistant speaking with %s.
                    Confirm intake is complete and they are ready for their visit%s.
                    """.formatted(
                    clinic,
                    patientName,
                    appointmentWhen == null || appointmentWhen.isBlank() ? "" : " on " + appointmentWhen).trim();
        };
    }

    public String smsFallbackBody(VoiceCallPurpose purpose, String clinicName) {
        String clinic = clinicName == null || clinicName.isBlank() ? properties.getDefaultClinicName() : clinicName;
        return switch (purpose) {
            case BOOKING -> "Hi from " + clinic
                    + " — we tried calling to help book your telehealth visit. Reply or call us back to schedule.";
            case REMINDER -> "Hi from " + clinic
                    + " — reminder about your upcoming telehealth appointment. Reply YES to confirm or call to reschedule.";
            case RESCHEDULE -> "Hi from " + clinic
                    + " — we tried calling about rescheduling your visit. Call us back when convenient.";
            case FOLLOW_UP -> "Hi from " + clinic
                    + " — quick follow-up from your care team. Reply if you need help before your visit.";
        };
    }

    public static String formatSlots(List<OpenSlot> slots) {
        if (slots == null || slots.isEmpty()) {
            return "- (no open slots loaded; ask the patient for preferred days/times and note them)";
        }
        return slots.stream()
                .limit(5)
                .map(s -> "- Provider " + s.providerId() + ": " + s.startTime() + " to " + s.endTime())
                .collect(Collectors.joining("\n"));
    }
}
