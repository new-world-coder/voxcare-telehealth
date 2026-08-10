package com.voxcare.voice.dto;

import com.voxcare.voice.model.VoiceRule;

import java.time.LocalDateTime;

public class VoiceRuleResponse {

    private Long id;
    private String name;
    private Boolean enabled;
    private Integer minQualificationScore;
    private Integer maxRetries;
    private Integer retryDelayMinutes;
    private Boolean smsFallbackEnabled;
    private String smsFallbackTemplate;
    private String outboundInstruction;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VoiceRuleResponse from(VoiceRule rule) {
        VoiceRuleResponse r = new VoiceRuleResponse();
        r.id = rule.getId();
        r.name = rule.getName();
        r.enabled = rule.getEnabled();
        r.minQualificationScore = rule.getMinQualificationScore();
        r.maxRetries = rule.getMaxRetries();
        r.retryDelayMinutes = rule.getRetryDelayMinutes();
        r.smsFallbackEnabled = rule.getSmsFallbackEnabled();
        r.smsFallbackTemplate = rule.getSmsFallbackTemplate();
        r.outboundInstruction = rule.getOutboundInstruction();
        r.priority = rule.getPriority();
        r.createdAt = rule.getCreatedAt();
        r.updatedAt = rule.getUpdatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Boolean getEnabled() { return enabled; }
    public Integer getMinQualificationScore() { return minQualificationScore; }
    public Integer getMaxRetries() { return maxRetries; }
    public Integer getRetryDelayMinutes() { return retryDelayMinutes; }
    public Boolean getSmsFallbackEnabled() { return smsFallbackEnabled; }
    public String getSmsFallbackTemplate() { return smsFallbackTemplate; }
    public String getOutboundInstruction() { return outboundInstruction; }
    public Integer getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
