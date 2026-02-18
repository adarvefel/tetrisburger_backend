package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.application.event.UserAdminImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateUserByAdmin;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateUserByAdminUseCase implements UpdateUserByAdmin {

    private static final Logger logger = LoggerFactory.getLogger(UpdateUserByAdminUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateUserByAdminUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User handle(UpdateUserByAdminCommand command) {
        logger.info("Admin {} actualizando usuario ID: {}", command.updatedBy(), command.idUser());

        User user = userRepository.findUserById(command.idUser())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Guardar old key para borrar después del commit (si hay nueva imagen)
        String oldImageKey = user.getUserImageKey();

        // Actualizar perfil completo (BD)
        user.updateByAdmin(
                command.userName(),
                command.email(),
                command.role(),
                command.phone(),
                command.updatedBy()
        );

        // Actualizar contraseña si se proporcionó
        if (command.password() != null && !command.password().isBlank()) {
            String hashedPassword = passwordEncoder.encode(command.password());
            user.resetPassword(hashedPassword, command.updatedBy());
        }

        // Guardar cambios de usuario (sin imagen todavía)
        User updatedUser = userRepository.saveUser(user);


        return updatedUser;
    }
}
