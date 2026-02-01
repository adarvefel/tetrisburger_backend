package com.tetris.tetrisburger_backend.application.event;

public record CustomBurgerImageUploadRequestedEvent(
        Integer idBurger,
        Integer idUser,
        byte[] fileBytes,
        String contentType,
        String originalFileName
) {
}