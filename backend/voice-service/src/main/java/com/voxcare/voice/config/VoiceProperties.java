package com.voxcare.voice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "voxcare.voice")
public class VoiceProperties {

    /** mock | dial */
    private String provider = "mock";
    private String dialApiKey = "";
    private String dialBaseUrl = "https://api.getdial.ai";
    private String dialFromNumberId = "";
    private String dialWebhookSecret = "";
    private int maxRetries = 3;
    private int retryDelayMinutes = 30;
    private boolean smsFallbackEnabled = true;
    private String defaultClinicName = "VoxCare";
    /** Base URL for patient-service (used to resolve phone by patientId). */
    private String patientServiceBaseUrl = "http://localhost:8082";
    private String providerServiceBaseUrl = "http://localhost:8083";
    private String appointmentServiceBaseUrl = "http://localhost:8084";
    private String smsFallbackBookingUrl = "http://localhost:3001/appointments";

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDialApiKey() {
        return dialApiKey;
    }

    public void setDialApiKey(String dialApiKey) {
        this.dialApiKey = dialApiKey;
    }

    public String getDialBaseUrl() {
        return dialBaseUrl;
    }

    public void setDialBaseUrl(String dialBaseUrl) {
        this.dialBaseUrl = dialBaseUrl;
    }

    public String getDialFromNumberId() {
        return dialFromNumberId;
    }

    public void setDialFromNumberId(String dialFromNumberId) {
        this.dialFromNumberId = dialFromNumberId;
    }

    public String getDialWebhookSecret() {
        return dialWebhookSecret;
    }

    public void setDialWebhookSecret(String dialWebhookSecret) {
        this.dialWebhookSecret = dialWebhookSecret;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getRetryDelayMinutes() {
        return retryDelayMinutes;
    }

    public void setRetryDelayMinutes(int retryDelayMinutes) {
        this.retryDelayMinutes = retryDelayMinutes;
    }

    public boolean isSmsFallbackEnabled() {
        return smsFallbackEnabled;
    }

    public void setSmsFallbackEnabled(boolean smsFallbackEnabled) {
        this.smsFallbackEnabled = smsFallbackEnabled;
    }

    public String getDefaultClinicName() {
        return defaultClinicName;
    }

    public void setDefaultClinicName(String defaultClinicName) {
        this.defaultClinicName = defaultClinicName;
    }

    public String getPatientServiceBaseUrl() {
        return patientServiceBaseUrl;
    }

    public void setPatientServiceBaseUrl(String patientServiceBaseUrl) {
        this.patientServiceBaseUrl = patientServiceBaseUrl;
    }

    public String getProviderServiceBaseUrl() {
        return providerServiceBaseUrl;
    }

    public void setProviderServiceBaseUrl(String providerServiceBaseUrl) {
        this.providerServiceBaseUrl = providerServiceBaseUrl;
    }

    public String getAppointmentServiceBaseUrl() {
        return appointmentServiceBaseUrl;
    }

    public void setAppointmentServiceBaseUrl(String appointmentServiceBaseUrl) {
        this.appointmentServiceBaseUrl = appointmentServiceBaseUrl;
    }

    public String getSmsFallbackBookingUrl() {
        return smsFallbackBookingUrl;
    }

    public void setSmsFallbackBookingUrl(String smsFallbackBookingUrl) {
        this.smsFallbackBookingUrl = smsFallbackBookingUrl;
    }
}
