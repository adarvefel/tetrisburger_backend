package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.CreateMenuCategoryCommand;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.UpdateMenuCategoryCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.CreateMenuCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.MenuCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.UpdateMenuCategoryRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuCategoryDtoMapper {

    CreateMenuCategoryCommand toCreateCommand(CreateMenuCategoryRequestDTO requestDTO);
    UpdateMenuCategoryCommand toUpdateCommand(Integer id, UpdateMenuCategoryRequestDTO requestDTO);
    MenuCategoryResponseDTO toResponseDTO(MenuCategory menuCategory);
}
