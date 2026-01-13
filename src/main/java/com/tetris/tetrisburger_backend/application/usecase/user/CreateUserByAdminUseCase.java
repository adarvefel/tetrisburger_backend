package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.exception.UserAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.CreateUserByAdmin;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.CreateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.port.out.ImageUploadResult;
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
    private final ImageStoragePort imageStoragePort;

    public CreateUserByAdminUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ImageStoragePort imageStoragePort
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public User handle(CreateUserByAdminCommand cmd) {
        logger.info("Admin {} creando usuario con email: {}", cmd.createdBy(), cmd.email());

        // 1. Verificar que el email no existe
        if (userRepository.existsByEmail(cmd.email())) {
            logger.warn("Email ya registrado: {}", cmd.email());
            throw new UserAlreadyExistsException("El email ya está registrado");
        }

        // 2. Subir imagen a S3 si existe
        String imageKey = null;
        String imageName = null;

        if (cmd.userImage() != null && !cmd.userImage().isEmpty()) {
            try {
                logger.info("Subiendo imagen a S3");
                ImageUploadResult uploadResult = imageStoragePort.uploadUserImage(cmd.userImage());

                if (uploadResult != null) {
                    imageKey = uploadResult.imageKey();
                    imageName = uploadResult.originalFileName();
                    logger.info("Imagen subida exitosamente: {}", imageKey);
                }
            } catch (Exception e) {
                logger.error("Error al subir imagen: {}", e.getMessage(), e);
                throw new ImageUploadException("No se pudo subir la imagen del usuario", e);
            }
        }

        // 3. Hashear contraseña
        String hashedPassword = passwordEncoder.encode(cmd.password());

        // 4. Crear usuario usando factory method del dominio
        User newUser = User.createByAdmin(
                cmd.userName(),
                cmd.email(),
                hashedPassword,
                cmd.role(),
                cmd.phone(),
                imageKey,
                imageName,
                cmd.createdBy()  // ✅ CAMBIO: usar cmd.createdBy() en vez de null
        );

        logger.debug("Usuario creado en dominio - createdBy: {}", newUser.getCreatedBy());

        // 5. Guardar usuario
        User savedUser = userRepository.saveUser(newUser);

        logger.info("Usuario creado exitosamente con ID: {} por admin: {}",
                savedUser.getIdUser(), savedUser.getCreatedBy());
        return savedUser;
    }
}
