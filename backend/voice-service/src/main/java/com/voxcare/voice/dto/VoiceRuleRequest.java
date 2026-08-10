package com.voxcare.voice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VoiceRuleRequest {

    @NotBlank
    private String name;

    private Boolean enabled = true;

    private Integer minQualificationScore = 70;

    private Integer maxRetries = 3;

    private Integer retryDelayMinutes = 30;

    private Boolean smsFallbackEnabled = true;

    private String smsFallbackTemplate;

    @NotBlank
    private String outboundInstruction;

    private Integer priority = 0;

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
}
