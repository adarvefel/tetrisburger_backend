package com.tetris.tetrisburger_backend.application.event;

import org.springframework.web.multipart.MultipartFile;

public record UserAdminImageChangeRequestedEvent(
        Integer idUser,
        byte[] newFileBytes,
        String contentType,
        String originalFileName,
        String oldImageKey,
        Integer updatedBy
) {}
