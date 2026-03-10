package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.command.UpdateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.BurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.CreateCustomBurgerRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.UpdateCustomBurgerRequestDTO;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BurgerRestDtoMapper {

    // ==================== CREATE MENU BURGER ====================
    default CreateBurgerCommand toCreateBurgerCommand(
            CreateMenuBurgerRequestDTO dto,
            MultipartFile burgerImage,
            Integer createdBy) {

        if (dto == null) return null;

        FileData imageData = burgerImage != null ? FileData.from(burgerImage) : null;

        List<CreateBurgerCommand.IngredientRequest> ingredients = dto.ingredients() != null
                ? dto.ingredients().stream()
                .map(ing -> new CreateBurgerCommand.IngredientRequest(
                        ing.idProduct(),
                        ing.quantity()
                ))
                .toList()
                : List.of();

        return new CreateBurgerCommand(
                dto.name(),
                dto.description(),
                imageData,
                dto.isFeatured(),
                dto.availability(),
                dto.finalPrice(),
                ingredients,
                createdBy
        );
    }

    // ==================== CREATE CUSTOM BURGER ====================
    default CreateCustomBurgerCommand toCreateCustomBurgerCommand(
            CreateCustomBurgerRequestDTO dto,
            Integer userId) {  // ← quita MultipartFile

        if (dto == null) return null;

        List<CreateCustomBurgerCommand.IngredientRequest> ingredients = dto.ingredients() != null
                ? dto.ingredients().stream()
                .map(ing -> new CreateCustomBurgerCommand.IngredientRequest(
                        ing.idProduct(),
                        ing.quantity()))
                .collect(Collectors.toList())
                : List.of();

        return new CreateCustomBurgerCommand(
                dto.name(),
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

        List<UpdateMenuBurgerCommand.IngredientRequest> ingredients = dto.ingredients() != null
                ? dto.ingredients().stream()
                .map(ing -> new UpdateMenuBurgerCommand.IngredientRequest(
                        ing.idProduct(),
                        ing.quantity()
                ))
                .collect(Collectors.toList())
                : List.of();

        return new UpdateMenuBurgerCommand(

                burgerId,
                dto.name(),
                dto.description(),
                dto.finalPrice(),
                dto.availability(),
                dto.isFeatured(),
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

        List<UpdateCustomBurgerCommand.IngredientRequest> ingredients = dto.ingredients() != null
                ? dto.ingredients().stream()
                .map(ing -> new UpdateCustomBurgerCommand.IngredientRequest(
                        ing.idProduct(),
                        ing.quantity()
                ))
                .collect(Collectors.toList())
                : List.of();

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
                burger.getFinalPrice(),
                burger.getImageUrl(),
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

    // ==================== INGREDIENTES - MENU BURGER ====================
    default MenuBurgerIngredientResponseDTO toMenuBurgerIngredientResponseDTO(BurgerIngredient ingredient) {
        if (ingredient == null) return null;

        return new MenuBurgerIngredientResponseDTO(
                ingredient.getIdBurgerIngredient(),
                ingredient.getIdProduct(),
                ingredient.getProductName(),
                ingredient.getPriceAtTime(),
                ingredient.getQuantity(),
                ingredient.calculateSubtotal(),
                ingredient.getIsOptional(),
                ingredient.getImageUrl() // ⚡ Usar imageUrl directamente evita NPE
        );
    }

    default List<MenuBurgerIngredientResponseDTO> toMenuBurgerIngredientResponseDTOList(List<BurgerIngredient> ingredients) {
        if (ingredients == null) return List.of();
        return ingredients.stream()
                .map(this::toMenuBurgerIngredientResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== INGREDIENTES - CUSTOM BURGER ====================
    default BurgerIngredientResponseDTO toBurgerIngredientResponseDTO(BurgerIngredient ingredient) {
        if (ingredient == null) return null;

        return new BurgerIngredientResponseDTO(
                ingredient.getIdBurgerIngredient(),
                ingredient.getIdProduct(),
                ingredient.getProductName(),
                ingredient.getPriceAtTime(),
                ingredient.getQuantity(),
                ingredient.calculateSubtotal(),
                ingredient.getImageUrl()
        );
    }

    default List<BurgerIngredientResponseDTO> toBurgerIngredientResponseDTOList(List<BurgerIngredient> ingredients) {
        if (ingredients == null) return List.of();
        return ingredients.stream()
                .map(this::toBurgerIngredientResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== HELPER: LocalDateTime → Instant ====================
    default java.time.Instant toInstant(java.time.LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}