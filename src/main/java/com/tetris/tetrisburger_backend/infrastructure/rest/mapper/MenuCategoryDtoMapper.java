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

    default CreateMenuCategoryCommand toCreateCommand(CreateMenuCategoryRequestDTO dto, Integer userId) {
        if (dto == null) return null;
        return new CreateMenuCategoryCommand(dto.menuCategoryName(), dto.description(), userId);
    }

    default UpdateMenuCategoryCommand toUpdateCommand(UpdateMenuCategoryRequestDTO dto, Integer userId) {
        if (dto == null) return null;
        return new UpdateMenuCategoryCommand(dto.menuCategoryName(), dto.description(), userId);
    }

    default MenuCategoryResponseDTO toResponseDTO(MenuCategory domain) {
        if (domain == null) return null;
        return new MenuCategoryResponseDTO(
                domain.getIdMenuCategory(),
                domain.getMenuCategoryName(),
                domain.getDescription(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getCreatedBy(),
                domain.getUpdatedBy()
        );
    }
}