// src/main/java/com/tetris/tetrisburger_backend/application/listener/UserProfileImageChangeListener.java
package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.UserProfileImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.port.out.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserProfileImageChangeListener {

    private final UserRepository userRepository;
    private final ImageStoragePort imageStoragePort;

    public UserProfileImageChangeListener(UserRepository userRepository, ImageStoragePort imageStoragePort) {
        this.userRepository = userRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserProfileImageChangeRequestedEvent event) {
        try {
            ImageUploadResult upload = imageStoragePort.uploadUserImage(event.newFile());
            if (upload == null) return;

            User user = userRepository.findUserById(event.idUser())
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

            user.updateImage(upload.imageKey(), upload.originalFileName(), event.updatedBy());
            userRepository.saveUser(user);

            if (event.oldImageKey() != null && !event.oldImageKey().isBlank()) {
                try {
                    imageStoragePort.deleteImage(event.oldImageKey());
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            throw new ImageUploadException("No se pudo subir la imagen del perfil", e);
        }
    }
}
