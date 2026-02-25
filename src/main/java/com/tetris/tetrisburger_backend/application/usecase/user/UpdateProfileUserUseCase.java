package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.application.event.UserProfileImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateProfileUserUseCase implements UpdateProfileUser {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateProfileUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User handle(Integer currentUserId, UpdateProfileUserCommand command) {

        if (!currentUserId.equals(command.idUser())) {
            throw new IllegalArgumentException("No tienes permiso para modificar este perfil");
        }

        User user = userRepository.findUserById(command.idUser())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (!user.isActive()) {
            throw new IllegalStateException("No puedes actualizar un usuario eliminado");
        }

        String oldImageKey = user.getUserImageKey();

        user.updateProfile(command.userName(), command.phone(), currentUserId);

        if (command.password() != null && !command.password().isBlank()) {
            String hashedPassword = passwordEncoder.encode(command.password());
            user.resetPassword(hashedPassword, currentUserId);
        }

        User updatedUser = userRepository.saveUser(user);



        return updatedUser;
    }
}
