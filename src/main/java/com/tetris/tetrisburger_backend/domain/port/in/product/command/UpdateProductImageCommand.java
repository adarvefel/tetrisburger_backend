package com.tetris.tetrisburger_backend.domain.port.in.product.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;


public record UpdateProductImageCommand(
        Integer id,
        FileData productImage,
        Integer updatedBy
) {
    public UpdateProductImageCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto es requerido");
        }
        if (productImage == null) {
            throw new IllegalArgumentException("La imagen del producto es requerida");
        }
        if (updatedBy == null) {
            throw new IllegalArgumentException("El ID del usuario que actualiza es requerido");
        }
    }
}
