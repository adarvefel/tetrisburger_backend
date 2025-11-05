package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateUserByAdmin;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateUserByAdminUseCase implements UpdateUserByAdmin {

    private static final Logger logger = LoggerFactory.getLogger(UpdateUserByAdminUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserByAdminUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * SOLO ADMIN: Puede actualizar ANY usuario con TODOS los campos
     */
    @Override
    public User handle(UpdateUserByAdminCommand command) {
        logger.info("Admin actualizando usuario ID: {}", command.idUser());

        // 1. Buscar usuario existente
        User existingUser = userRepository.findUserById(command.idUser())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado - ID: {}", command.idUser());
                    return new UserNotFoundException("Usuario no encontrado");
                });

        // 2. Actualizar campos (Admin puede actualizar TODO)
        if (command.userName() != null && !command.userName().isBlank()) {
            existingUser.setUserName(command.userName());
        }

        if (command.email() != null && !command.email().isBlank()) {
            existingUser.setEmail(command.email());
        }

        if (command.password() != null && !command.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(command.password()));
        }

        if (command.phone() != null && !command.phone().isBlank()) {
            existingUser.setPhone(command.phone());
        }

        if (command.userImage() != null && !command.userImage().isBlank()) {
            existingUser.setUserImage(command.userImage());
        }

        if (command.role() != null) {
            existingUser.setRole(command.role());
        }

        // 3. Guardar cambios
        User updatedUser = userRepository.saveUser(existingUser);

        logger.info("Usuario actualizado exitosamente - ID: {}", updatedUser.getIdUser());
        return updatedUser;
    }
}
