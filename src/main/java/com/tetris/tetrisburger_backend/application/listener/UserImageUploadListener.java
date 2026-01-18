package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.UserImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.port.out.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserImageUploadListener {

    private static final Logger logger = LoggerFactory.getLogger(UserImageUploadListener.class);

    private final UserRepository userRepository;
    private final ImageStoragePort imageStoragePort;

    public UserImageUploadListener(UserRepository userRepository, ImageStoragePort imageStoragePort) {
        this.userRepository = userRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserImageUploadRequested(UserImageUploadRequestedEvent event) {
        try {
            ImageUploadResult upload = imageStoragePort.uploadUserImage(event.file());
            if (upload == null) return;

            User user = userRepository.findUserById(event.idUser())
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

            user.updateImage(upload.imageKey(), upload.originalFileName(), event.performedBy());
            userRepository.saveUser(user);

            logger.info("Imagen subida y usuario actualizado: userId={}", event.idUser());
        } catch (Exception e) {
            throw new ImageUploadException("No se pudo subir la imagen del usuario", e);
        }
    }
}
