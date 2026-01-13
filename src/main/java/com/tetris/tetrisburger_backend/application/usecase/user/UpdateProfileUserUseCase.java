package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.port.out.ImageUploadResult;
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
    private final ImageStoragePort imageStoragePort;

    public UpdateProfileUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ImageStoragePort imageStoragePort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public User handle(Integer currentUserId, UpdateProfileUserCommand command) {
        logger.debug("Usuario {} actualizando perfil", currentUserId);

        // 1. Validación de seguridad
        if (!currentUserId.equals(command.idUser())) {
            logger.error("Usuario {} intentando modificar perfil de {}", currentUserId, command.idUser());
            throw new IllegalArgumentException("No tienes permiso para modificar este perfil");
        }

        // 2. Obtener usuario
        User user = userRepository.findUserById(command.idUser())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado - ID: {}", command.idUser());
                    return new UserNotFoundException("Usuario no encontrado");
                });

        // 3. Verificar que está activo
        if (!user.isActive()) {
            logger.error("Intento de actualizar usuario eliminado - ID: {}", command.idUser());
            throw new IllegalStateException("No puedes actualizar un usuario eliminado");
        }

        // 4. Actualizar imagen si se proporcionó
        if (command.userImage() != null && !command.userImage().isEmpty()) {

            // Eliminar imagen anterior de S3 si existe
            if (user.getUserImageKey() != null) {
                try {
                    imageStoragePort.deleteImage(user.getUserImageKey());
                    logger.info("Imagen anterior eliminada: {}", user.getUserImageKey());
                } catch (Exception e) {
                    logger.warn("No se pudo eliminar imagen anterior: {}", e.getMessage());
                    // No fallar si no se puede eliminar la imagen anterior
                }
            }

            // Subir nueva imagen a S3
            try {
                logger.info("Subiendo nueva imagen a S3 para usuario ID: {}", command.idUser());
                ImageUploadResult uploadResult = imageStoragePort.uploadUserImage(command.userImage());

                if (uploadResult != null) {
                    String imageKey = uploadResult.imageKey();
                    String imageName = uploadResult.originalFileName();
                    user.updateImage(imageKey, imageName, currentUserId);
                    logger.info("Nueva imagen subida exitosamente: {}", imageKey);
                }
            } catch (Exception e) {
                logger.error("Error al subir imagen para usuario {}: {}", command.idUser(), e.getMessage(), e);
                throw new ImageUploadException("No se pudo subir la imagen del perfil", e);
            }
        }

        // 5. Actualizar perfil básico (nombre y teléfono)
        user.updateProfile(
                command.userName(),
                command.phone(),
                currentUserId
        );

        // 6. Actualizar contraseña si se proporcionó
        if (command.password() != null && !command.password().isBlank()) {
            String hashedPassword = passwordEncoder.encode(command.password());
            user.resetPassword(hashedPassword, currentUserId);
            logger.info("Contraseña actualizada para usuario ID: {}", command.idUser());
        }

        // 7. Guardar cambios
        User updatedUser = userRepository.saveUser(user);

        logger.info("Perfil actualizado exitosamente - Usuario ID: {}", updatedUser.getIdUser());
        return updatedUser;
    }
}
