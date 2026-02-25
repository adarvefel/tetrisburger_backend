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
        if (cmd.userImage() != null && cmd.userImage().isValid()) {
            eventPublisher.publishEvent(new UserImageUploadRequestedEvent(
                    savedUser.getIdUser(),
                    cmd.userImage().bytes(),           // ✅ byte[]
                    cmd.userImage().contentType(),     // ✅ String
                    cmd.userImage().originalFilename(), // ✅ String
                    cmd.createdBy()
            ));
        }

        return savedUser;
    }
}
