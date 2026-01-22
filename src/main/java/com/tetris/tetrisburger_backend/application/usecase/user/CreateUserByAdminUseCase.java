package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.application.event.UserImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.UserAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.CreateUserByAdmin;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.CreateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Transactional
public class CreateUserByAdminUseCase implements CreateUserByAdmin {

    private static final Logger logger = LoggerFactory.getLogger(CreateUserByAdminUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public CreateUserByAdminUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User handle(CreateUserByAdminCommand cmd) {
        logger.info("Admin {} creando usuario con email: {}", cmd.createdBy(), cmd.email());

        if (userRepository.existsByEmail(cmd.email())) {
            throw new UserAlreadyExistsException("El email ya está registrado");
        }

        String hashedPassword = passwordEncoder.encode(cmd.password());

        // Crear usuario sin imagen todavía
        User newUser = User.createByAdmin(
                cmd.userName(),
                cmd.email(),
                hashedPassword,
                cmd.role(),
                cmd.phone(),
                null,
                null,
                cmd.createdBy()
        );

        User savedUser = userRepository.saveUser(newUser);

        // Publicar evento: subir imagen después del commit
        if (cmd.userImage() != null && !cmd.userImage().isEmpty()) {
            var file = cmd.userImage();
            try {
                eventPublisher.publishEvent(new UserImageUploadRequestedEvent(
                        savedUser.getIdUser(),
                        file.getBytes(),
                        file.getContentType(),
                        file.getOriginalFilename(),
                        cmd.createdBy()
                ));
            } catch (IOException e) {
                throw new RuntimeException("No se pudo leer la imagen del usuario", e);
                // o tu ImageUploadException si prefieres
            }
        }


        return savedUser;
    }
}
