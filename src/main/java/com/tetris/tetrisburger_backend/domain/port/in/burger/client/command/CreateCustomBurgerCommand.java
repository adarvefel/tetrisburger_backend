package com.tetris.tetrisburger_backend.domain.port.in.burger.client.command;

import java.util.List;

public record CreateCustomBurgerCommand(
        String name,
        Integer idUser,
        List<IngredientRequest> ingredients
) {
    public CreateCustomBurgerCommand {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        if (ingredients == null || ingredients.isEmpty())
            throw new IllegalArgumentException("Debe incluir al menos un ingrediente");
        if (idUser == null)
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
    }

    public record IngredientRequest(
            Integer idProduct,
            Integer quantity) {
        public IngredientRequest {
            if (idProduct == null || idProduct <= 0)
                throw new IllegalArgumentException("ID producto debe ser positivo");
            if (quantity == null || quantity <= 0)
                throw new IllegalArgumentException("Cantidad debe ser > 0");
        }
    }
}