package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.whatsapp.WhatsappSettingsResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WhatsappSettingsRestMapper {

    default WhatsappSettingsResponseDTO toResponseDTO(WhatsappSettings s) {
        return new WhatsappSettingsResponseDTO(
                s.getIdSettings(),
                s.getBusinessNumber(),
                s.getApiKey(),
                s.getMessageTemplate(),
                s.isAutoSend(),
                s.getUpdatedAt()
        );
    }
}