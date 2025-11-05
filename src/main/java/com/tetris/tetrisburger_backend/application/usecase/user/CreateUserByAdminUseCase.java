package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.CreateUserByAdmin;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.CreateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CreateUserByAdminUseCase implements CreateUserByAdmin {

    private static final Logger logger = LoggerFactory.getLogger(CreateUserByAdminUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserByAdminUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User handle(CreateUserByAdminCommand cmd) {
        logger.info("Admin creando usuario con email: {}", cmd.email());

        // 1. Verificar que el email no existe
        if (userRepository.existsByEmail(cmd.email())) {
            logger.warn("Email ya registrado: {}", cmd.email());
            throw new UserAlreadyExistsException("El email ya está registrado");
        }

        // 2. Crear usuario (usar setters, no constructor con 13 parámetros)
        User newUser = new User();
        newUser.setUserName(cmd.userName());
        newUser.setEmail(cmd.email());
        newUser.setPassword(passwordEncoder.encode(cmd.password()));
        newUser.setPhone(cmd.phone());
        newUser.setUserImage(cmd.userImage());
        newUser.setRole(cmd.role());
        // Los demás (createdAt, updatedAt, etc.) se manejan en la Entity con @CreationTimestamp

        // 3. Guardar usuario
        // userRepository.saveUser() usará UserEntityMapper.toEntity() internamente
        User savedUser = userRepository.saveUser(newUser);

        logger.info("Usuario creado exitosamente con ID: {}", savedUser.getIdUser());
        return savedUser;
    }
}
