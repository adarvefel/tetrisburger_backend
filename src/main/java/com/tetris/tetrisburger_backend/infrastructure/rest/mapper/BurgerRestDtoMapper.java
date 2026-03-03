package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.*;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.user.BurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.user.CreateCustomBurgerRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.user.UpdateCustomBurgerRequestDTO;
import org.mapstruct.Mapper;

import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BurgerRestDtoMapper {

    // ==================== CREATE MENU BURGER ====================

    // En tu BurgerRestDtoMapper
    default CreateBurgerCommand toCreateBurgerCommand(
            CreateMenuBurgerRequestDTO dto,
            MultipartFile burgerImage,
            Integer createdBy) {

        if (dto == null) return null;

        FileData imageData = burgerImage != null ? FileData.from(burgerImage) : null;
        ImageStatus imageStatus = imageData != null ? ImageStatus.PENDING : ImageStatus.NONE;

        List<CreateBurgerCommand.IngredientRequest> ingredients = dto.ingredients().stream()
                .map(ing -> new CreateBurgerCommand.IngredientRequest(
                        ing.idProduct(),
                        ing.quantity()
                ))
                .toList();

        return new CreateBurgerCommand(
                dto.name(),
                dto.description(),
                imageData,
                ingredients,
                dto.isFeatured(),
                dto.finalPrice(),
                createdBy
        );
    }

    // ==================== CREATE CUSTOM BURGER ====================

    default CreateCustomBurgerCommand toCreateCustomBurgerCommand(
            CreateCustomBurgerRequestDTO dto,
            MultipartFile burgerImage,
            Integer userId) {

        if (dto == null) return null;

        FileData imageData = FileData.from(burgerImage);

        List<CreateCustomBurgerCommand.IngredientRequest> ingredients = dto.ingredients() == null
                ? List.of()
                : dto.ingredients().stream()
                .map(ing -> new CreateCustomBurgerCommand.IngredientRequest(
                        ing.idProduct(),
                        ing.quantity()
                ))
                .collect(Collectors.toList());

        return new CreateCustomBurgerCommand(
                dto.name(),
                dto.description(),
                imageData,
                userId,
                ingredients
        );
    }

    // ==================== UPDATE MENU BURGER ====================

    default UpdateMenuBurgerCommand toUpdateMenuBurgerCommand(
            Integer burgerId,
            UpdateMenuBurgerRequestDTO dto,
            Integer updatedBy) {

        if (dto == null) return null;

        List<UpdateMenuBurgerCommand.IngredientRequest> ingredients = dto.ingredients() == null
                ? List.of()
                : dto.ingredients().stream()
                .map(ing -> new UpdateMenuBurgerCommand.IngredientRequest(
                        ing.idProduct(),
                        ing.quantity()
                ))
                .collect(Collectors.toList());

        return new UpdateMenuBurgerCommand(
                burgerId,
                dto.name(),
                dto.description(),
                dto.availability(),
                ingredients,
                updatedBy
        );
    }

    // ==================== UPDATE CUSTOM BURGER ====================

    default UpdateCustomBurgerCommand toUpdateCustomBurgerCommand(
            Integer burgerId,
            UpdateCustomBurgerRequestDTO dto,
            Integer userId) {

        if (dto == null) return null;

        List<UpdateCustomBurgerCommand.IngredientRequest> ingredients = dto.ingredients() == null
                ? List.of()
                : dto.ingredients().stream()
                .map(ing -> new UpdateCustomBurgerCommand.IngredientRequest(
                        ing.idProduct(),
                        ing.quantity()
                ))
                .collect(Collectors.toList());

        return new UpdateCustomBurgerCommand(
                burgerId,
                userId,
                dto.name(),
                dto.description(),
                ingredients
        );
    }

    // ==================== RESPONSE: MENU BURGER ====================

    default MenuBurgerResponseDTO toMenuBurgerResponseDTO(Burger burger) {
        if (burger == null) return null;

        return new MenuBurgerResponseDTO(
                burger.getIdBurger(),
                burger.getName(),
                burger.getDescription(),
                burger.getBasePrice(),
                burger.getFinalPrice(),
                burger.calculateMargin(),
                burger.calculateMarginPercentage(),
                burger.isSellingAtLoss(),
                burger.isOnMenu(),
                burger.isFeatured(),
                burger.isAvailability(),
                burger.getImageUrl(),
                burger.getImageKey(),
                burger.getImageStatus(),
                burger.getTimesOrdered(),
                toMenuBurgerIngredientResponseDTOList(burger.getIngredients()),
                burger.getCreatedAt(),
                burger.getUpdatedAt(),
                burger.getCreatedBy(),
                burger.getUpdatedBy()
        );
    }

    default List<MenuBurgerResponseDTO> toMenuBurgerResponseDTOList(List<Burger> burgers) {
        if (burgers == null) return List.of();
        return burgers.stream()
                .map(this::toMenuBurgerResponseDTO)
                .collect(Collectors.toList());
    }

    default MenuBurgerPageResponseDTO toMenuBurgerPageResponseDTO(PageResponse<Burger> page) {
        if (page == null) return null;

        return new MenuBurgerPageResponseDTO(
                toMenuBurgerResponseDTOList(page.content()),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages()
        );
    }

    // ==================== RESPONSE: CUSTOM BURGER ====================

    default BurgerResponseDTO toBurgerResponseDTO(Burger burger) {
        if (burger == null) return null;

        return new BurgerResponseDTO(
                burger.getIdBurger(),
                burger.getName(),
                burger.getDescription(),
                burger.getFinalPrice(),
                burger.isFeatured(),
                burger.isAvailability(),
                burger.getImageUrl(),
                burger.getImageStatus(),
                burger.getTimesOrdered(),
                toBurgerIngredientResponseDTOList(burger.getIngredients())
        );
    }

    default List<BurgerResponseDTO> toBurgerResponseDTOList(List<Burger> burgers) {
        if (burgers == null) return List.of();
        return burgers.stream()
                .map(this::toBurgerResponseDTO)
                .collect(Collectors.toList());
    }

    default BurgerPageResponseDTO toBurgerPageResponseDTO(PageResponse<Burger> page) {
        if (page == null) return null;

        return new BurgerPageResponseDTO(
                toBurgerResponseDTOList(page.content()),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages()
        );
    }

    // ==================== RESPONSE: INGREDIENTES - MENU BURGER ====================

    /**
     * Convierte BurgerIngredient → MenuBurgerIngredientResponseDTO
     * Para hamburguesas del menú (vista de administrador)
     */
    default MenuBurgerIngredientResponseDTO toMenuBurgerIngredientResponseDTO(BurgerIngredient ingredient) {
        if (ingredient == null) return null;

        return new MenuBurgerIngredientResponseDTO(
                ingredient.getIdBurgerIngredient(),
                ingredient.getIdProduct(),
                ingredient.getProductName(),
                ingredient.getPriceAtTime(),
                ingredient.getQuantity(),
                ingredient.calculateSubtotal()
        );
    }

    default List<MenuBurgerIngredientResponseDTO> toMenuBurgerIngredientResponseDTOList(
            List<BurgerIngredient> ingredients) {
        if (ingredients == null) return List.of();
        return ingredients.stream()
                .map(this::toMenuBurgerIngredientResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== RESPONSE: INGREDIENTES - CUSTOM BURGER ====================

    /**
     * Convierte BurgerIngredient → BurgerIngredientResponseDTO
     * Para hamburguesas personalizadas (vista de cliente)
     */
    default BurgerIngredientResponseDTO toBurgerIngredientResponseDTO(BurgerIngredient ingredient) {
        if (ingredient == null) return null;

        return new BurgerIngredientResponseDTO(
                ingredient.getIdBurgerIngredient(),
                ingredient.getIdProduct(),
                ingredient.getProductName(),
                ingredient.getPriceAtTime(),
                ingredient.getQuantity(),
                ingredient.calculateSubtotal()
        );
    }

    default List<BurgerIngredientResponseDTO> toBurgerIngredientResponseDTOList(
            List<BurgerIngredient> ingredients) {
        if (ingredients == null) return List.of();
        return ingredients.stream()
                .map(this::toBurgerIngredientResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== HELPER: LocalDateTime → Instant ====================

    /**
     * Convierte LocalDateTime a Instant (UTC) para respuestas JSON
     */
    default java.time.Instant toInstant(java.time.LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}
