package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.application.event.UserProfileImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateProfileImage;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileImageCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateProfileImageUseCase implements UpdateProfileImage {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateProfileImageUseCase(UserRepository userRepository,
                                     ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User handle(Integer currentUserId, UpdateProfileImageCommand command) {
        if (!currentUserId.equals(command.idUser())) {
            throw new IllegalArgumentException("No tienes permiso para modificar este perfil");
        }
        if (!currentUserId.equals(command.updatedBy())) {
            throw new IllegalArgumentException("updatedBy no coincide con el usuario autenticado");
        }
        if (command.fileBytes() == null || command.fileBytes().length == 0) {
            throw new IllegalArgumentException("fileBytes es requerido");
        }
        if (command.contentType() == null || command.contentType().isBlank()) {
            throw new IllegalArgumentException("contentType es requerido");
        }
        if (command.originalFileName() == null || command.originalFileName().isBlank()) {
            throw new IllegalArgumentException("originalFileName es requerido");
        }

        User user = userRepository.findUserById(command.idUser())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        String oldImageKey = user.getUserImageKey();

        eventPublisher.publishEvent(new UserProfileImageChangeRequestedEvent(
                command.idUser(),
                command.fileBytes(),
                command.contentType(),
                command.originalFileName(),
                oldImageKey,
                command.updatedBy()
        ));

        return user;
    }
}
