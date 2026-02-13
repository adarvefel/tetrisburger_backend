package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateMenuBurgerImage;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerImageCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateMenuBurgerImageUseCase implements UpdateMenuBurgerImage {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMenuBurgerImageUseCase.class);

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_CONTENT_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    };

    private final BurgerRepository burgerRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateMenuBurgerImageUseCase(
            BurgerRepository burgerRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.burgerRepository = burgerRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Burger handle(UpdateMenuBurgerImageCommand command) {
        logger.info("Updating menu burger image: burgerId={}, adminId={}",
                command.idBurger(), command.updatedBy());

        try {
            // 1. Validaciones
            validateCommand(command);
            validateUser(command.updatedBy());
            validateImageData(command.imageData());

            // 2. Buscar burger de menú
            Burger burger = burgerRepository.findActiveMenuById(command.idBurger())
                    .orElseThrow(() -> new BurgerNotFoundException(command.idBurger()));

            // 3. Validar que es menu burger
            if (!burger.isMenuBurger()) {
                throw new InvalidBurgerException(
                        "Solo hamburguesas de menú pueden actualizar imagen. Burger ID: "
                                + command.idBurger()
                );
            }

            logger.info("Publishing MenuBurgerImageUploadRequestedEvent for burgerId: {}",
                    burger.getIdBurger());

            // 4. Publicar evento para subir imagen de forma asíncrona
            eventPublisher.publishEvent(
                    new MenuBurgerImageUploadRequestedEvent(
                            burger.getIdBurger(),
                            command.imageData().bytes(),
                            command.imageData().contentType(),
                            command.imageData().originalFilename(),
                            command.updatedBy()
                    )
            );

            logger.info("Menu burger image update event published successfully: burgerId={}",
                    burger.getIdBurger());

            // 5. Retornar burger (la imagen se actualizará de forma asíncrona)
            return burger;

        } catch (BurgerNotFoundException | UserNotFoundException |
                 InvalidBurgerException | ImageUploadException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating menu burger image: burgerId={}",
                    command.idBurger(), e);
            throw new ImageUploadException("Error updating menu burger image", e);
        }
    }

    private void validateCommand(UpdateMenuBurgerImageCommand command) {
        if (command == null) {
            throw new InvalidBurgerException("Command cannot be null");
        }

        if (command.idBurger() == null) {
            throw new InvalidBurgerException("Burger ID cannot be null");
        }

        if (command.updatedBy() == null) {
            throw new InvalidBurgerException("Admin ID cannot be null");
        }

        if (command.imageData() == null) {
            throw new ImageUploadException("Image data cannot be null");
        }
    }

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void validateImageData(com.tetris.tetrisburger_backend.domain.common.FileData imageData) {
        // Validar bytes
        if (imageData.bytes() == null || imageData.bytes().length == 0) {
            throw new ImageUploadException("La imagen no puede estar vacía");
        }

        // Validar tamaño (máximo 5MB)
        if (imageData.bytes().length > MAX_IMAGE_SIZE) {
            throw new ImageUploadException(
                    String.format("La imagen no puede superar %d MB. Tamaño actual: %.2f MB",
                            MAX_IMAGE_SIZE / (1024 * 1024),
                            imageData.bytes().length / (1024.0 * 1024.0))
            );
        }

        // Validar tipo de contenido
        String contentType = imageData.contentType();
        if (contentType == null || contentType.isBlank()) {
            throw new ImageUploadException("El tipo de contenido de la imagen es requerido");
        }

        boolean isValidType = false;
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (contentType.toLowerCase().startsWith(allowedType)) {
                isValidType = true;
                break;
            }
        }

        if (!isValidType) {
            throw new ImageUploadException(
                    "Solo se permiten imágenes JPG, PNG o WebP. Tipo recibido: " + contentType
            );
        }

        // Validar nombre de archivo
        if (imageData.originalFilename() == null || imageData.originalFilename().isBlank()) {
            throw new ImageUploadException("El nombre del archivo es requerido");
        }

        logger.debug("Image validation passed: size={}KB, type={}, filename={}",
                imageData.bytes().length / 1024,
                contentType,
                imageData.originalFilename());
    }
}
