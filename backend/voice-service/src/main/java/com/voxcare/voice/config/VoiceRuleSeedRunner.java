package com.voxcare.voice.config;

import com.voxcare.voice.model.VoiceRule;
import com.voxcare.voice.repository.VoiceRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds EstateCraft-equivalent Dial voice rules when the table is empty.
 */
@Component
public class VoiceRuleSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(VoiceRuleSeedRunner.class);

    private final VoiceRuleRepository repository;

    public VoiceRuleSeedRunner(VoiceRuleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }
        VoiceRule booking = new VoiceRule();
        booking.setName("Appointment Booking Outreach");
        booking.setEnabled(true);
        booking.setMinQualificationScore(0);
        booking.setMaxRetries(3);
        booking.setRetryDelayMinutes(30);
        booking.setSmsFallbackEnabled(true);
        booking.setSmsFallbackTemplate(
                "Hi {{leadName}}, this is VoxCare. We tried calling to help book your telehealth visit. Reply YES or call us back to schedule!");
        booking.setOutboundInstruction(
                "You are a friendly telehealth scheduling assistant for VoxCare. Greet {{leadName}} warmly, offer available appointment slots, and book the time they confirm. Be concise and HIPAA-aware: do not discuss diagnoses.");
        booking.setPriority(10);

        VoiceRule reminder = new VoiceRule();
        reminder.setName("Appointment Reminder Follow-up");
        reminder.setEnabled(true);
        reminder.setMinQualificationScore(0);
        reminder.setMaxRetries(2);
        reminder.setRetryDelayMinutes(60);
        reminder.setSmsFallbackEnabled(true);
        reminder.setSmsFallbackTemplate(
                "Hi {{leadName}}, VoxCare reminder: you have an upcoming telehealth appointment. Reply YES to confirm or call to reschedule.");
        reminder.setOutboundInstruction(
                "You are calling on behalf of VoxCare. Remind {{leadName}} about their upcoming telehealth appointment and offer to reschedule if needed. Do not discuss clinical details.");
        reminder.setPriority(5);

        repository.save(booking);
        repository.save(reminder);
        log.info("Seeded EstateCraft-compatible Dial voice rules");
    }
}
