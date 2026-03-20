package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.UserAdminImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserAdminImageChangeListener {

    private final UserRepository userRepository;
    private final ImageStoragePort imageStoragePort;

    public UserAdminImageChangeListener(UserRepository userRepository, ImageStoragePort imageStoragePort) {
        this.userRepository = userRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserAdminImageChangeRequestedEvent event) {
        try {
            // 1) Subir nueva imagen (fuera de transacción)
            ImageUploadResult upload = imageStoragePort.uploadUserImage(
                    event.newFileBytes(),
                    event.contentType(),
                    event.originalFileName()
            );

            if (upload == null) return;

            // 2) Guardar metadata en BD
            persistNewImageKey(event.idUser(), upload, event.updatedBy());

            // 3) Borrar anterior (best-effort)
            if (event.oldImageKey() != null && !event.oldImageKey().isBlank()) {
                try {
                    imageStoragePort.deleteImage(event.oldImageKey());
                } catch (Exception e) {

                }
            }

        } catch (Exception e) {
            throw new ImageUploadException("No se pudo subir la imagen del usuario", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void persistNewImageKey(Integer idUser, ImageUploadResult upload, Integer updatedBy) {
        User user = userRepository.findUserById(idUser)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        String imageUrl = imageStoragePort.getImageUrl(upload.imageKey());
        user.updateImage(upload.imageKey(),imageUrl, updatedBy);
        userRepository.saveUser(user);
    }
}
