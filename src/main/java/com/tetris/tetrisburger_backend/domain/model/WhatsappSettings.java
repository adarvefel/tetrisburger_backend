package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDateTime;

public class WhatsappSettings {

    private Integer idSettings;
    private String businessNumber;
    private String messageTemplate;
    private String apiKey;
    private boolean autoSend;
    private LocalDateTime updatedAt;

    private WhatsappSettings() {}

    public static WhatsappSettings reconstitute(
            Integer idSettings, String businessNumber, String apiKey,
            String messageTemplate, boolean autoSend,
            LocalDateTime updatedAt
    ) {
        WhatsappSettings s = new WhatsappSettings();
        s.idSettings = idSettings;
        s.businessNumber = businessNumber;
        s.apiKey = apiKey;
        s.messageTemplate = messageTemplate;
        s.autoSend = autoSend;
        s.updatedAt = updatedAt;
        return s;
    }

    public void update(String businessNumber, String apiKey, String messageTemplate, boolean autoSend) {
        this.businessNumber = businessNumber;
        this.apiKey = apiKey;
        this.messageTemplate = messageTemplate;
        this.autoSend = autoSend;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getIdSettings()      { return idSettings; }
    public String getBusinessNumber()   { return businessNumber; }
    public String getMessageTemplate()  { return messageTemplate; }
    public boolean isAutoSend()         { return autoSend; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getApiKey() { return apiKey; }

}