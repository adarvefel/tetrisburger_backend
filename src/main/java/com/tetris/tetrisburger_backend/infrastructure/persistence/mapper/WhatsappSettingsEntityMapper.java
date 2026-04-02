    package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;
    
    import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;
    import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.WhatsappSettingsEntity;
    import org.mapstruct.Mapper;
    
    @Mapper(componentModel = "spring")
    public interface WhatsappSettingsEntityMapper {
    
        default WhatsappSettings toDomain(WhatsappSettingsEntity e) {
            if (e == null) return null;
            return WhatsappSettings.reconstitute(
                    e.getIdSettings(),
                    e.getBusinessNumber(),
                    e.getApiKey(),
                    e.getMessageTemplate(),
                    e.isAutoSend(),
                    e.getUpdatedAt()
            );
        }
    
        default WhatsappSettingsEntity toEntity(WhatsappSettings s) {
            if (s == null) return null;
            WhatsappSettingsEntity e = new WhatsappSettingsEntity();
            e.setIdSettings(s.getIdSettings());
            e.setBusinessNumber(s.getBusinessNumber());
            e.setApiKey(s.getApiKey());
            e.setMessageTemplate(s.getMessageTemplate());
            e.setAutoSend(s.isAutoSend());
            e.setUpdatedAt(s.getUpdatedAt());
            return e;
        }
    }