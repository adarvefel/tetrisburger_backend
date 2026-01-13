package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateUserByAdmin;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserByAdminCommand;
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
public class UpdateUserByAdminUseCase implements UpdateUserByAdmin {

    private static final Logger logger = LoggerFactory.getLogger(UpdateUserByAdminUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageStoragePort imageStoragePort;

    public UpdateUserByAdminUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ImageStoragePort imageStoragePort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public User handle(UpdateUserByAdminCommand command) {
        logger.info("Admin {} actualizando usuario ID: {}", command.updatedBy(), command.idUser());

        // 1. Buscar usuario existente
        User user = userRepository.findUserById(command.idUser())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado - ID: {}", command.idUser());
                    return new UserNotFoundException("Usuario no encontrado");
                });

        // 2. Actualizar imagen si se proporcionó
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
                    user.updateImage(imageKey, imageName, command.updatedBy());
                    logger.info("Nueva imagen subida exitosamente: {}", imageKey);
                }
            } catch (Exception e) {
                logger.error("Error al subir imagen para usuario {}: {}", command.idUser(), e.getMessage(), e);
                throw new ImageUploadException("No se pudo subir la imagen del usuario", e);
            }
        }

        // 3. Actualizar perfil completo usando método de dominio
        user.updateByAdmin(
                command.userName(),
                command.email(),
                command.role(),
                command.phone(),
                command.updatedBy()
        );

        // 4. Actualizar contraseña si se proporcionó
        if (command.password() != null && !command.password().isBlank()) {
            String hashedPassword = passwordEncoder.encode(command.password());
            user.resetPassword(hashedPassword, command.updatedBy());
            logger.info("Contraseña actualizada para usuario ID: {}", command.idUser());
        }

        // 5. Guardar cambios
        User updatedUser = userRepository.saveUser(user);

        logger.info("Usuario actualizado exitosamente - ID: {} por admin: {}",
                updatedUser.getIdUser(), command.updatedBy());
        return updatedUser;
    }
}
