package com.tetris.tetrisburger_backend.application.event;

public record MenuBurgerImageUploadRequestedEvent(
        Integer idBurger,
        byte[] fileBytes,
        String contentType,
        String originalFileName,
        Integer uploadedBy
) {
}
