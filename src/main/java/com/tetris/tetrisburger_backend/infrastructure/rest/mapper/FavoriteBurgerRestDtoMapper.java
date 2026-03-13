package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurgerDetail;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.BurgerIngredientResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.BurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerSimpleResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoriteBurgerRestDtoMapper {

    default FavoriteBurgerResponseDTO toResponseDTO(FavoriteBurger favorite, Burger burger) {
        if (favorite == null) return null;

        BurgerResponseDTO burgerDTO = burger != null
                ? new BurgerResponseDTO(
                burger.getIdBurger(),
                burger.getName(),
                burger.getFinalPrice(),
                burger.getImageUrl(),
                toIngredientDTOList(burger.getIngredients()))
                : null;

        return new FavoriteBurgerResponseDTO(
                favorite.getIdFavorite(),
                favorite.getIdUser(),
                burgerDTO,
                favorite.getCreatedAt()
        );
    }

    default BurgerIngredientResponseDTO toIngredientDTO(BurgerIngredient ingredient) {
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

    default List<BurgerIngredientResponseDTO> toIngredientDTOList(List<BurgerIngredient> ingredients) {
        if (ingredients == null) return List.of();
        return ingredients.stream()
                .map(this::toIngredientDTO)
                .toList();
    }

    default List<FavoriteBurgerResponseDTO> toResponseDTOList(List<FavoriteBurgerDetail> details) {
        if (details == null) return List.of();
        return details.stream()
                .map(d -> toResponseDTO(d.getFavorite(), d.getBurger()))
                .toList();
    }

    default FavoriteBurgerSimpleResponseDTO toSimpleResponseDTO(FavoriteBurger favorite) {
        if (favorite == null) return null;
        return new FavoriteBurgerSimpleResponseDTO(
                favorite.getIdFavorite(),
                favorite.getIdUser(),
                favorite.getIdBurger(),
                favorite.getName(),
                favorite.getCreatedAt()
        );
    }
}
