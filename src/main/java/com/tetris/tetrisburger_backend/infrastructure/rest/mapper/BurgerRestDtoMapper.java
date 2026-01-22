package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BurgerRestDtoMapper {

    // ========= Custom burger: Request DTO -> Command =========


    CreateCustomBurgerCommand toCustomCommand(CreateCustomBurgerRequestDTO request,Integer idUser);

    @Mapping(source = "idProduct", target = "idProduct")
    IngredientRequest toIngredientRequest(IngredientRequestDTO dto);

    List<IngredientRequest> toIngredientRequestList(List<IngredientRequestDTO> dtos);

    // ========= Custom burger: Domain -> Response DTO =========

    @Mapping(source = "onMenu",   target = "isOnMenu")
    @Mapping(source = "favorite", target = "isFavorite")
    @Mapping(source = "custom",   target = "isCustom")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedBy", target = "deletedBy")
    BurgerResponseDTO toResponse(Burger burger);

    List<BurgerResponseDTO> toResponseList(List<Burger> burgers);

    BurgerIngredientResponseDTO toIngredientResponse(BurgerIngredient ingredient);

    List<BurgerIngredientResponseDTO> toIngredientResponseList(List<BurgerIngredient> ingredients);

    // ========= Menu burger: Request DTO -> Command =========

    @Mapping(source = "name",        target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "imageUrl",    target = "imageUrl")
    @Mapping(source = "ingredients", target = "ingredients")
    @Mapping(source = "favorite",    target = "isFavorite")
    CreateBurgerCommand toMenuCommand(CreateMenuBurgerRequestDTO request);


    UpdateMenuBurgerCommand toUpdateMenuBurgerCommand(
            Integer idBurger,
            UpdateMenuBurgerRequestDTO dto
    );


    // ========= Menu burger: Domain -> Response DTO =========

    @Mapping(source = "onMenu",   target = "isOnMenu")
    @Mapping(source = "favorite", target = "isFavorite")
    @Mapping(source = "custom",   target = "isCustom")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedBy", target = "deletedBy")
    MenuBurgerResponseDTO toMenuResponse(Burger burger);

    List<MenuBurgerResponseDTO> toMenuResponseList(List<Burger> burgers);

    MenuBurgerIngredientResponseDTO toMenuIngredientResponse(BurgerIngredient ingredient);

    List<MenuBurgerIngredientResponseDTO> toMenuIngredientResponseList(List<BurgerIngredient> ingredients);

    // ========= PageResponse<Burger> -> MenuBurgerPageResponseDTO =========

    default MenuBurgerPageResponseDTO toMenuBurgerPageResponseDTO(PageResponse<Burger> page) {
        List<MenuBurgerResponseDTO> content = toMenuResponseList(page.content());
        return new MenuBurgerPageResponseDTO(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    UpdateCustomBurgerCommand toUpdateCustomBurgerCommand(
            Integer idBurger,
            Integer idUser,
            UpdateCustomBurgerRequestDTO dto
    );

    // ========= PageResponse<Burger> -> BurgerPageResponseDTO (custom/mine) =========

    default BurgerPageResponseDTO toBurgerPageResponseDTO(PageResponse<Burger> page) {
        List<BurgerResponseDTO> content = toResponseList(page.content());
        return new BurgerPageResponseDTO(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    // ========= Conversores de fechas Instant <-> LocalDateTime =========

    default LocalDateTime map(Instant value) {
        return value == null ? null : LocalDateTime.ofInstant(value, ZoneOffset.UTC);
    }

    default Instant map(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
