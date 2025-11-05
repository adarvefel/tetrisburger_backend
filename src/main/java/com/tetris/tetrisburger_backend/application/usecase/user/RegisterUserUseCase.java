package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.RegisterUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.RegisterUserCommand;
import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RegisterUserUseCase implements RegisterUser {

    private static final Logger logger = LoggerFactory.getLogger(RegisterUserUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailPort emailPort;

    public RegisterUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailPort emailPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailPort = emailPort;
    }

    @Override
    public User handle(RegisterUserCommand cmd) {
        logger.info("Registrando usuario con email: {}", cmd.email());

        // 1.  Verificar que el email no existe
        if (userRepository.existsByEmail(cmd.email())) {
            logger.warn("Intento de registrar email duplicado: {}", cmd.email());
            throw new UserAlreadyExistsException("El email ya está registrado");
        }

        // 2. Crear usuario (solo con setters)
        User newUser = new User();
        newUser.setUserName(cmd.userName());
        newUser.setEmail(cmd.email());
        newUser.setPassword(passwordEncoder.encode(cmd.password()));
        newUser.setRole(Role.CLIENT);

        // 3. Guardar usuario
        User savedUser = userRepository.saveUser(newUser);
        logger.info("Usuario registrado exitosamente - ID: {}", savedUser.getIdUser());

        // 4. Enviar email de bienvenida
        emailPort.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUserName());
        logger.info("Email de bienvenida enviado a: {}", savedUser.getEmail());

        return savedUser;
    }
}
