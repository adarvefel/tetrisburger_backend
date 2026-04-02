package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.*;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.CreateMenuCategoryCommand;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.UpdateMenuCategoryCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.DeleteResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.CreateMenuCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.MenuCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.UpdateMenuCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.MenuCategoryDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MenuCategoryController extends AuthenticatedController {

    private final CreateMenuCategory createMenuCategory;
    private final UpdateMenuCategory updateMenuCategory;
    private final DeleteMenuCategory deleteMenuCategory;
    private final GetMenuCategoryById getMenuCategoryById;
    private final ListMenuCategory listMenuCategory;
    private final MenuCategoryDtoMapper menuCategoryDtoMapper;

    public MenuCategoryController(
            CreateMenuCategory createMenuCategory,
            UpdateMenuCategory updateMenuCategory,
            DeleteMenuCategory deleteMenuCategory,
            GetMenuCategoryById getMenuCategoryById,
            ListMenuCategory listMenuCategory,
            MenuCategoryDtoMapper menuCategoryDtoMapper) {
        this.createMenuCategory = createMenuCategory;
        this.updateMenuCategory = updateMenuCategory;
        this.deleteMenuCategory = deleteMenuCategory;
        this.getMenuCategoryById = getMenuCategoryById;
        this.listMenuCategory = listMenuCategory;
        this.menuCategoryDtoMapper = menuCategoryDtoMapper;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping("/menu-category")
    public ResponseEntity<MenuCategoryResponseDTO> create(
            @Valid @RequestBody CreateMenuCategoryRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = getUserId(userDetails);
        CreateMenuCategoryCommand command = menuCategoryDtoMapper.toCreateCommand(requestDTO, userId);
        MenuCategory category = createMenuCategory.create(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(menuCategoryDtoMapper.toResponseDTO(category));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PutMapping("/menu-category/{id}")
    public ResponseEntity<MenuCategoryResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMenuCategoryRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = getUserId(userDetails);
        UpdateMenuCategoryCommand command = menuCategoryDtoMapper.toUpdateCommand(requestDTO, userId);
        MenuCategory category = updateMenuCategory.handle(id, command);
        return ResponseEntity.ok(menuCategoryDtoMapper.toResponseDTO(category));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @DeleteMapping("/menu-category/{id}")
    public ResponseEntity<DeleteResponseDTO> delete(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = getUserId(userDetails);
        MenuCategory category = deleteMenuCategory.handle(id, userId);
        return ResponseEntity.ok(new DeleteResponseDTO(
                "Categoría eliminada correctamente",
                true,
                new DeleteResponseDTO.DeletedResourceDTO(
                        category.getIdMenuCategory(),
                        category.getMenuCategoryName()
                )
        ));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/menu-category/{id}")
    public ResponseEntity<MenuCategoryResponseDTO> getById(@PathVariable Integer id) {
        MenuCategory category = getMenuCategoryById.handle(id);
        return ResponseEntity.ok(menuCategoryDtoMapper.toResponseDTO(category));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/menu-categories")
    public ResponseEntity<PageResponse<MenuCategoryResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<MenuCategory> result = listMenuCategory.handle(pagination);
        return ResponseEntity.ok(result.map(menuCategoryDtoMapper::toResponseDTO));
    }
}