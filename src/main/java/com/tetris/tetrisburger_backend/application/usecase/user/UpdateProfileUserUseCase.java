package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateProfileUserUseCase implements UpdateProfileUser {

    private static final Logger logger = LoggerFactory.getLogger(UpdateProfileUserUseCase.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateProfileUserUseCase(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User handle(Integer currentUserId, UpdateProfileUserCommand command) {
        logger.debug("Usuario {} actualizando perfil", currentUserId);

        // Validación de seguridad
        if (!currentUserId.equals(command.idUser())) {
            throw new IllegalArgumentException("No tienes permiso");
        }

        // Obtener usuario
        User existingUser = userRepository.findUserById(command.idUser())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Actualizar solo campos NO null
        if (command.userName() != null) {
            existingUser.setUserName(command.userName());
        }
        if (command.password() != null) {
            existingUser.setPassword(passwordEncoder.encode(command.password()));
        }
        if (command.userImage() != null) {
            existingUser.setUserImage(command.userImage());
        }
        if (command.phone() != null) {
            existingUser.setPhone(command.phone());
        }



        return userRepository.saveUser(existingUser);
    }
}
