package com.voxcare.voice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Voice outreach rule — field-for-field match of EstateCraft VoiceRule.
 */
@Entity
@Table(name = "voice_rules")
public class VoiceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "min_qualification_score", nullable = false)
    private Integer minQualificationScore = 70;

    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;

    @Column(name = "retry_delay_minutes", nullable = false)
    private Integer retryDelayMinutes = 30;

    @Column(name = "sms_fallback_enabled", nullable = false)
    private Boolean smsFallbackEnabled = true;

    @Column(name = "sms_fallback_template", columnDefinition = "TEXT")
    private String smsFallbackTemplate;

    @Column(name = "outbound_instruction", nullable = false, columnDefinition = "TEXT")
    private String outboundInstruction;

    @Column(nullable = false)
    private Integer priority = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (enabled == null) enabled = true;
        if (minQualificationScore == null) minQualificationScore = 70;
        if (maxRetries == null) maxRetries = 3;
        if (retryDelayMinutes == null) retryDelayMinutes = 30;
        if (smsFallbackEnabled == null) smsFallbackEnabled = true;
        if (priority == null) priority = 0;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public Integer getMinQualificationScore() { return minQualificationScore; }
    public void setMinQualificationScore(Integer minQualificationScore) { this.minQualificationScore = minQualificationScore; }
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    public Integer getRetryDelayMinutes() { return retryDelayMinutes; }
    public void setRetryDelayMinutes(Integer retryDelayMinutes) { this.retryDelayMinutes = retryDelayMinutes; }
    public Boolean getSmsFallbackEnabled() { return smsFallbackEnabled; }
    public void setSmsFallbackEnabled(Boolean smsFallbackEnabled) { this.smsFallbackEnabled = smsFallbackEnabled; }
    public String getSmsFallbackTemplate() { return smsFallbackTemplate; }
    public void setSmsFallbackTemplate(String smsFallbackTemplate) { this.smsFallbackTemplate = smsFallbackTemplate; }
    public String getOutboundInstruction() { return outboundInstruction; }
    public void setOutboundInstruction(String outboundInstruction) { this.outboundInstruction = outboundInstruction; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
