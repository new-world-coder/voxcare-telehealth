package com.voxcare.voice.orchestrator;

import com.voxcare.voice.model.VoiceRule;
import com.voxcare.voice.service.PatientInfo;

/**
 * EstateCraft-compatible voice rule config view used by the orchestrator.
 */
public record VoiceRuleConfig(
        Long id,
        String name,
        boolean enabled,
        int minQualificationScore,
        int maxRetries,
        int retryDelayMinutes,
        boolean smsFallbackEnabled,
        String smsFallbackTemplate,
        String outboundInstruction,
        int priority
) {
    public static VoiceRuleConfig from(VoiceRule rule) {
        return new VoiceRuleConfig(
                rule.getId(),
                rule.getName(),
                Boolean.TRUE.equals(rule.getEnabled()),
                rule.getMinQualificationScore() == null ? 70 : rule.getMinQualificationScore(),
                rule.getMaxRetries() == null ? 3 : rule.getMaxRetries(),
                rule.getRetryDelayMinutes() == null ? 30 : rule.getRetryDelayMinutes(),
                !Boolean.FALSE.equals(rule.getSmsFallbackEnabled()),
                rule.getSmsFallbackTemplate(),
                rule.getOutboundInstruction(),
                rule.getPriority() == null ? 0 : rule.getPriority());
    }

    public static VoiceRuleConfig defaultRule() {
        return new VoiceRuleConfig(
                null,
                "Default",
                true,
                70,
                3,
                30,
                true,
                "Hi {{patientName}}, we tried reaching you from VoxCare about your telehealth appointment. Reply or call us back to schedule!",
                "You are a friendly VoxCare telehealth scheduling assistant. Greet {{patientName}} warmly and help book or confirm their visit.",
                0);
    }

    /** EstateCraft uses {{leadName}}; VoxCare also accepts {{patientName}}. */
    public String renderInstruction(PatientInfo patient) {
        return personalize(outboundInstruction, patient);
    }

    public String renderSms(PatientInfo patient) {
        String template = (smsFallbackTemplate == null || smsFallbackTemplate.isBlank())
                ? "Hi {{patientName}}, we tried reaching you about your VoxCare telehealth appointment. Reply or call us back!"
                : smsFallbackTemplate;
        return personalize(template, patient);
    }

    private static String personalize(String template, PatientInfo patient) {
        String fullName = patient == null ? "there" : patient.displayName();
        String first = patient == null || patient.firstName() == null || patient.firstName().isBlank()
                ? "there"
                : patient.firstName();
        return template
                .replace("{{leadName}}", fullName)
                .replace("{{patientName}}", fullName)
                .replace("{{firstName}}", first);
    }
}
