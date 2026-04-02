package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_settings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhatsappSettingsEntity {

    @Id
    @Column(name = "id_settings")
    private Integer idSettings;

    @Column(name = "business_number", length = 20)
    private String businessNumber;

    @Column(name = "api_key", length = 50)
    private String apiKey;

    @Column(name = "message_template", columnDefinition = "TEXT")
    private String messageTemplate;


    @Column(name = "auto_send")
    private boolean autoSend;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}