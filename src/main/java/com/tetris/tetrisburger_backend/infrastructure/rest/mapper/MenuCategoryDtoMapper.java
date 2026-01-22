package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.CreateMenuCategoryCommand;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuCategoryEntity;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.CreateMenuCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.CreateMenuCategoryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuCategoryDtoMapper {

    CreateMenuCategoryCommand toCreateCommand(CreateMenuCategoryRequestDTO requestDTO);

    CreateMenuCategoryResponseDTO toResponseDTO(MenuCategory menuCategory);

}
