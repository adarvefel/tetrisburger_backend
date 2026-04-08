package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.AdditionSettingsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdditionSettingsEntityMapper {
    AdditionSettings toDomain(AdditionSettingsEntity entity);
    AdditionSettingsEntity toEntity(AdditionSettings settings);
}