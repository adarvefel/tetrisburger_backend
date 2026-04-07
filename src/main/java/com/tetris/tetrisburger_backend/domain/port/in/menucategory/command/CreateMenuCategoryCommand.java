package com.tetris.tetrisburger_backend.domain.port.in.menucategory.command;

public record CreateMenuCategoryCommand(
        String menuCategoryName,
        String  description,
        Integer createdBy
) {

    public CreateMenuCategoryCommand {
        if(menuCategoryName == null  || menuCategoryName.isBlank()){
            throw new IllegalArgumentException("El nombre de la categoria es obligatorio");

        }
    }
}
