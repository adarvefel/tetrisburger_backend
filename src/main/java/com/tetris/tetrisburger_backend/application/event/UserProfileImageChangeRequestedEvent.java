package com.tetris.tetrisburger_backend.application.event;

import org.springframework.web.multipart.MultipartFile;

public record UserProfileImageChangeRequestedEvent(
        Integer idUser,
        MultipartFile newFile,
        String oldImageKey,
        Integer updatedBy
) {}
