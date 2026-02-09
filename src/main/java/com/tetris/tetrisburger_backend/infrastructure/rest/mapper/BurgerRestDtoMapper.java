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
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BurgerRestDtoMapper {

    // ==================== CREATE MENU BURGER ====================

    default CreateBurgerCommand toCreateBurgerCommand(
            CreateMenuBurgerRequestDTO dto,
            MultipartFile burgerImage,
            Integer userId) {

        if (dto == null) return null;

        FileData imageData = (burgerImage != null && !burgerImage.isEmpty())
                ? FileData.from(burgerImage)
                : null;

        List<CreateBurgerCommand.IngredientRequest> ingredients = dto.ingredients() == null
                ? List.of()
                : dto.ingredients().stream()
                .map(ing -> new CreateBurgerCommand.IngredientRequest(
                        ing.idProduct(),
                        ing.quantity(),
                        ing.isOptional()
                ))
                .toList();

        return new CreateBurgerCommand(
                dto.name(),
                dto.description(),
                imageData,
                ingredients,
                dto.isFavorite() != null ? dto.isFavorite() : false,
                dto.finalPrice(),
                userId
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
                        ing.quantity(),
                        ing.isOptional()
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
                        ing.quantity(),
                        ing.isOptional()
                ))
                .collect(Collectors.toList());

        return new UpdateMenuBurgerCommand(
                burgerId,
                dto.name(),
                dto.description(),
                ingredients,
                dto.availability(),
                dto.isFavorite(),
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
                        ing.quantity(),
                        ing.isOptional()
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
                burger.isFavorite(),
                burger.isCustom(),
                burger.isAvailability(),
                burger.getImageUrl(),
                burger.getImageKey(),
                burger.getImageStatus().name(),
                burger.getTimesOrdered(),
                toMenuBurgerIngredientResponseDTOList(burger.getIngredients()),
                burger.getCreatedAt(),
                burger.getUpdatedAt(),
                burger.getDeletedAt(),
                burger.getCreatedBy(),
                burger.getUpdatedBy(),
                burger.getDeletedBy()
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
                burger.getBasePrice(),
                burger.getFinalPrice(),
                burger.isOnMenu(),
                burger.isFavorite(),
                burger.isCustom(),
                burger.isAvailability(),
                burger.getImageUrl(),
                burger.getImageKey(),
                burger.getImageStatus().name(),
                burger.getIdUser(),
                burger.getTimesOrdered(),
                toBurgerIngredientResponseDTOList(burger.getIngredients()),
                burger.getCreatedAt(),
                burger.getUpdatedAt()
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
                ingredient.calculateSubtotal(),
                ingredient.isOptional()
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
                ingredient.calculateSubtotal(),
                ingredient.isOptional()
        );
    }

    default List<BurgerIngredientResponseDTO> toBurgerIngredientResponseDTOList(
            List<BurgerIngredient> ingredients) {
        if (ingredients == null) return List.of();
        return ingredients.stream()
                .map(this::toBurgerIngredientResponseDTO)
                .collect(Collectors.toList());
    }



}
