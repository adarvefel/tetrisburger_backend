package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;

import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.command.UpdateAdditionSettingsCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionsettings.UpdateAdditionSettingsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionsettings.AdditionSettingsResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdditionSettingsDtoMapper {
    UpdateAdditionSettingsCommand toCommand(UpdateAdditionSettingsRequestDTO requestDTO);
    AdditionSettingsResponseDTO toResponseDTO(AdditionSettings settings);
}