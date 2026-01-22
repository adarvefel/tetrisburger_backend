package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.CreateMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.CreateMenuCategoryCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.CreateMenuCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.CreateMenuCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.MenuCategoryDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
 @RequestMapping("/api")
public class MenuCategoryController {
    private final CreateMenuCategory createMenuCategory;
    private final MenuCategoryDtoMapper menuCategoryDtoMapper;

    public MenuCategoryController(CreateMenuCategory createMenuCategory, MenuCategoryDtoMapper menuCategoryDtoMapper) {
        this.createMenuCategory = createMenuCategory;
        this.menuCategoryDtoMapper = menuCategoryDtoMapper;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @PostMapping("/menu-category")
    public ResponseEntity<CreateMenuCategoryResponseDTO>create(
            @Valid @RequestBody CreateMenuCategoryRequestDTO  requestDTO
    )
    {
        CreateMenuCategoryCommand command = menuCategoryDtoMapper.toCreateCommand(requestDTO);
        MenuCategory category = createMenuCategory.create(command);
        CreateMenuCategoryResponseDTO responseDTO = menuCategoryDtoMapper.toResponseDTO(category);
        return  ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);


    }

}
