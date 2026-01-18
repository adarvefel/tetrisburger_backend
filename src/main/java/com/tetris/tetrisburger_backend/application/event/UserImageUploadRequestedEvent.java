package com.tetris.tetrisburger_backend.application.event;

import org.springframework.web.multipart.MultipartFile;

public record UserImageUploadRequestedEvent(
        Integer idUser,
        MultipartFile file,
        Integer performedBy
) {
}
