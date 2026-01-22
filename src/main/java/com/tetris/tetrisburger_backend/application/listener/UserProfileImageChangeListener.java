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
import org.springframework.transaction.annotation.Propagation;
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
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserProfileImageChangeRequestedEvent event) {
        try {
            // 1) Subir nueva imagen
            ImageUploadResult upload = imageStoragePort.uploadUserImage(
                    event.fileBytes(),
                    event.contentType(),
                    event.originalFileName()
            );
            if (upload == null) return;

            // 2) Guardar metadata en BD (transacción nueva)
            persistUserImage(event.idUser(), upload, event.updatedBy());

            // 3) Borrar anterior (best-effort)
            if (event.oldImageKey() != null && !event.oldImageKey().isBlank()) {
                try {
                    imageStoragePort.deleteImage(event.oldImageKey());
                } catch (Exception ignored) { }
            }
        } catch (Exception e) {
            throw new ImageUploadException("No se pudo subir la imagen del perfil", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void persistUserImage(Integer idUser, ImageUploadResult upload, Integer updatedBy) {
        User user = userRepository.findUserById(idUser)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        user.updateImage(upload.imageKey(), upload.originalFileName(), updatedBy);
        userRepository.saveUser(user);
    }
}
